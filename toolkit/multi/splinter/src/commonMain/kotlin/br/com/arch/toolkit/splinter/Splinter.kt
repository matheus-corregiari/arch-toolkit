@file:Suppress(
    "TooManyFunctions",
    "unused",
    "CanBeParameter"
)
@file:OptIn(ExperimentalAtomicApi::class, ExperimentalTime::class)

package br.com.arch.toolkit.splinter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import br.com.arch.toolkit.lumber.Lumber
import br.com.arch.toolkit.result.DataResult
import br.com.arch.toolkit.splinter.Splinter.ExecutionPolicy.CancelWhenHasRunningBeforeStart
import br.com.arch.toolkit.splinter.Splinter.ExecutionPolicy.IgnoreWhenHasRunningOperations
import br.com.arch.toolkit.splinter.Splinter.ExecutionPolicy.ParallelQueue
import br.com.arch.toolkit.splinter.Splinter.ExecutionPolicy.SequentialQueue
import br.com.arch.toolkit.splinter.Splinter.StopPolicy.OnLifecycle
import br.com.arch.toolkit.splinter.extension.incrementAsId
import br.com.arch.toolkit.splinter.extension.info
import br.com.arch.toolkit.splinter.extension.invokeCatching
import br.com.arch.toolkit.splinter.extension.lazyJob
import br.com.arch.toolkit.splinter.extension.tryError
import br.com.arch.toolkit.splinter.extension.tryInfo
import br.com.arch.toolkit.splinter.strategy.Strategy
import br.com.arch.toolkit.util.dataResultError
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Unconfined
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.plusAssign
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class Splinter<RETURN> internal constructor(
    private val id: String,
    private val config: Config<RETURN>,
    private val strategy: Strategy<RETURN>,
    val resultHolder: ResponseDataHolder<RETURN> = ResultHolder(),
    val messageHolder: DataHolder<Message> = MessageHolder()
) {

    init {
        (resultHolder as ResultHolder).init(this)
        (messageHolder as MessageHolder).init(this)
    }

    /**
     * Atomic Holders
     */
    //region Atomic Holders
    private val operationCount = AtomicInt(0)
    private val operationStartedCount = AtomicInt(0)
    private val operationCompletedCount = AtomicInt(0)
    private val eventCount = AtomicInt(0)
    private val logCount = AtomicInt(0)
    //endregion

    /**
     * Flows that holds everything
     */
    //region Flows
    private val apprenticeFlow = MutableSharedFlow<Apprentice<RETURN>>(
        replay = 10, extraBufferCapacity = 5, onBufferOverflow = BufferOverflow.SUSPEND
    )
    internal val dataFlow = MutableSharedFlow<DataResult<RETURN>>(
        replay = 500, extraBufferCapacity = 50, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    internal val logFlow = MutableSharedFlow<Message>(
        replay = 500, extraBufferCapacity = 50, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    //endregion

    /**
     * Auxiliary Fields
     */
    //region Auxiliary Fields
    private val lock = Object()
    private val logger: Lumber.Oak get() = Lumber.tag(id).quiet(config.quiet)
    private val observer = object : DefaultLifecycleObserver {
        init {
            config.lifecycleOwner?.lifecycle?.addObserver(this)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            if (config.stopPolicy == OnLifecycle) {
                logger.warn("[Splinter] Killed due lifecycle")
                kill()
            } else {
                logger.warn("[Splinter] Canceled due lifecycle")
                cancel()
            }
        }
    }
    private val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
        logFlow.tryError("[Splinter] Major failed detected", throwable)
        dataFlow.tryEmit(dataResultError(throwable))
    }
    //endregion

    /**
     * Coroutine Running Jobs
     */
    //region Jobs
    private val masterJob = SupervisorJob().apply {
        invokeOnCompletion { logger.warn("[Splinter] Master job stopped") }
    }
    private val logJob by createJob(tag = "Log", scope = CoroutineScope(Unconfined)) {
        val eventCountJob = launch { dataFlow.collect { eventCount.plusAssign(1) } }
        val logCountJob = launch { logFlow.collect { logCount.plusAssign(1) } }
        if (config.quiet) listOf(eventCountJob, logCountJob).joinAll()
        else logFlow.collect { logger.log(it.level, it.error, it.indentedMessage) }
    }

    private val collectJob by createJob(tag = "Collect", scope = config.scope) {
        apprenticeFlow.collect { apprentice ->
            val state = if (apprentice.start()) "Started!" else "Collect!"
            logFlow.info("[Splinter] - Apprentice ${apprentice.id} - $state")
            operationStartedCount.plusAssign(1)
            val log = launch { logFlow.emitAll(apprentice.logChannel) }
            val data = launch { dataFlow.emitAll(apprentice.dataChannel) }
            runCatching { listOf(log, data).joinAll() }
            apprentice.stop()
            operationCompletedCount.plusAssign(1)
            logFlow.info("[Splinter] - Apprentice ${apprentice.id} - Finished!")
            if (config.stopPolicy == StopPolicy.AfterFirstExecution) {
                logFlow.info("[Splinter] - Killing after first execution!")
                kill()
            }
        }
    }
    //endregion

    /**
     * Useful flags
     */
    //region Flags
    val isKilled: Boolean get() = masterJob.isActive.not()
    val isRunning: Boolean get() = !isKilled && (hasRunningApprentice || isStarting || moreCreatedThenCompleted)
    val isStarting: Boolean get() = moreCreatedThenStarted
    val hasRunningApprentice: Boolean
        get() = apprenticeFlow.replayCache.any(Apprentice<RETURN>::isRunning)

    private val moreCreatedThenStarted: Boolean
        get() = operationCount.load() > operationStartedCount.load()
    private val moreCreatedThenCompleted: Boolean
        get() = operationCount.load() > operationCompletedCount.load()
    //endregion

    fun execute() = synchronized(lock) {
        if (shouldProceedToExecute().not()) return@synchronized
        if (logJob.start()) logger.warn("[Splinter] - Log - Started")
        if (collectJob.start()) logger.warn("[Splinter] - Collect - Started")
        apprenticeFlow.tryEmit(
            value = Apprentice(
                id = operationCount.incrementAsId(),
                scope = config.scope + masterJob,
                holder = resultHolder,
                strategy = strategy,
            ).also { apprentice ->
                if (config.policy == ParallelQueue) {
                    logFlow.tryInfo("[Splinter] - Apprentice ${apprentice.id} - Started!")
                    apprentice.start()
                } else {
                    logFlow.tryInfo("[Splinter] - Apprentice ${apprentice.id} - Enqueued!")
                }
            }
        )
    }.let { resultHolder }

    fun cancel() = runCatching {
        if (isRunning.not()) return@runCatching
        val runningApprentices = apprenticeFlow.replayCache
            .filter { it.isRunning && it.isClosing.not() }
            .ifEmpty { return@runCatching }
        for (apprentice in runningApprentices) {
            logFlow.tryInfo("[Splinter] Cancel - ${apprentice.id}")
            apprentice.cancel()
        }
        config.onCancel?.invokeCatching()
    }.getOrDefault(Unit)

    fun kill(): ResponseDataHolder<RETURN> = synchronized(lock) {
        runCatching {
            if (isKilled) return@synchronized resultHolder
            if (isRunning) cancel()
            for (apprentice in apprenticeFlow.replayCache) apprentice.stop()
            masterJob.cancel()
            logger.warn("[Splinter] Game over!")
        }.getOrDefault(Unit)
        return@synchronized resultHolder
    }

    suspend fun await(): DataResult<RETURN> {
        if (isRunning.not()) return resultHolder.get()
        for (apprentice in apprenticeFlow.replayCache) apprentice.await()
        masterJob.children.dropWhile { it == collectJob || it == logJob }
            .forEach { runCatching { it.join() } }
        return resultHolder.get()
    }

    @Suppress("MagicNumber")
    override fun toString() = """
        ------------------------------------------------------------
        ${"| [Splinter $id] - Statistics ".padEnd(60, '-')}
        ------------------------------------------------------------
        | - Counters:
        |     - created ---> ${operationCount.load()}
        |     - started ---> ${operationStartedCount.load()}
        |     - completed -> ${operationCompletedCount.load()}
        |     - cache -----> ${apprenticeFlow.replayCache.size}
        |     - event -----> ${eventCount.load()}
        |     - log -------> ${logCount.load()}
        ------------------------------------------------------------
        | - Jobs:
        |     - master --> ${masterJob.isActive}
        |     - log -----> ${operationCount.load() != 0 && logJob.isActive}
        |     - collect -> ${operationCount.load() != 0 && collectJob.isActive}
        |     - children -> ${
        masterJob.children.mapIndexed { i, job -> "[$i] - ${job.isActive}" }.toList()
    }
        ------------------------------------------------------------
        | - Data:
        |     - status -> ${resultHolder.status}
        |     - data ---> ${resultHolder.data}
        |     - error --> ${resultHolder.error}       
        ------------------------------------------------------------
        | - Flags:
        |     - isRunning ----------------> $isRunning
        |     - isKilled -----------------> $isKilled
        |     - isStarting ---------------> $isStarting
        |     - hasRunningApprentice -----> $hasRunningApprentice
        |     - moreCreatedThenStarted ---> $moreCreatedThenStarted
        |     - moreCreatedThenCompleted -> $moreCreatedThenCompleted
        ------------------------------------------------------------
    """.trimIndent().trimStart()

    /* -------------------------------------------------------------------------------------------*/
    /* Private Methods -----------------------------------------------------------------------------------*/
    /* -------------------------------------------------------------------------------------------*/
    //region Private methods
    @Suppress("CyclomaticComplexMethod")
    private fun shouldProceedToExecute(): Boolean {
        val shouldProceed = AtomicBoolean(true)
        when {
            isKilled -> {
                logger.warn("[Splinter] Already dead - skipping")
                shouldProceed.store(false)
            }

            operationCount.load() > 0 && config.stopPolicy == StopPolicy.AfterFirstExecution -> {
                logger.warn("[Splinter] Should die after first execution - skipping")
                shouldProceed.store(false)
            }

            else -> when (config.policy) {
                ParallelQueue, SequentialQueue -> if (isStarting || isRunning)
                    logFlow.tryInfo("[Splinter] Adding to execution queue")

                IgnoreWhenHasRunningOperations -> if (isStarting || isRunning) {
                    logFlow.tryInfo("[Splinter] Already running - skipping")
                    shouldProceed.store(false)
                }

                CancelWhenHasRunningBeforeStart -> when {
                    isStarting -> {
                        logFlow.tryInfo("[Splinter] - Has starting execution - skipping")
                        shouldProceed.store(false)
                    }

                    isRunning -> {
                        logFlow.tryInfo("[Splinter] Canceling and add to execution queue")
                        cancel()
                    }
                }
            }
        }
        return shouldProceed.load()
    }

    private inline fun createJob(
        tag: String,
        scope: CoroutineScope,
        crossinline func: suspend CoroutineScope.() -> Unit
    ) = (scope + masterJob + exceptionHandler).lazyJob(
        onCreate = { println("[Splinter] - $tag - Created") },
        job = {
            logger.warn("[Splinter] - $tag - Initialized")
            func()
        },
        onComplete = { logger.warn("[Splinter] - $tag - Completed") }
    )
    //endregion

    /* -------------------------------------------------------------------------------------------*/
    /* Classes -----------------------------------------------------------------------------------*/
    /* -------------------------------------------------------------------------------------------*/
    //region Classes
    @ConsistentCopyVisibility
    data class Config<T> private constructor(
        val scope: CoroutineScope,
        val quiet: Boolean,
        val lifecycleOwner: LifecycleOwner?,
        val policy: ExecutionPolicy,
        val stopPolicy: StopPolicy,
        val onCancel: (() -> Unit)?
    ) {
        companion object Creator {
            operator fun <T> invoke(config: Builder<T>.() -> Unit = {}) =
                Builder<T>().apply(config).build()
        }

        class Builder<T> internal constructor() {

            private var scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
            private var quiet: Boolean = true
            private var lifecycleOwner: LifecycleOwner? = null
            private var policy: ExecutionPolicy = IgnoreWhenHasRunningOperations
            private var stopPolicy: StopPolicy = OnLifecycle
            private var onCancel: (() -> Unit)? = null

            fun scope(scope: CoroutineScope) = apply { this.scope = scope }
            fun logging(enabled: Boolean) = apply { this.quiet = enabled.not() }
            fun owner(owner: LifecycleOwner) = apply { this.lifecycleOwner = owner }
            fun policy(policy: ExecutionPolicy) = apply { this.policy = policy }
            fun stop(policy: StopPolicy) = apply { this.stopPolicy = policy }
            fun invokeOnCancel(listener: () -> Unit) = apply { this.onCancel = listener }

            internal fun build() = Config<T>(
                scope = scope,
                quiet = quiet,
                lifecycleOwner = lifecycleOwner,
                policy = policy,
                stopPolicy = stopPolicy,
                onCancel = onCancel,
            )
        }
    }

    @ConsistentCopyVisibility
    data class Message private constructor(
        val level: Lumber.Level,
        val message: String?,
        val error: Throwable?,
        val timestamp: Long = Clock.System.now().toEpochMilliseconds()
    ) {

        internal val indentedMessage = message?.indented()

        private fun String?.messageTag(): String? {
            if (isNullOrBlank()) return null
            val matches = "(\\[.*])".toRegex().findAll(this)
            return matches.firstOrNull()?.groupValues?.firstOrNull()
        }

        private fun String?.indented(): String? {
            val messageTag = messageTag() ?: return this
            val indent =
                identMap.firstNotNullOfOrNull { (regex, indent) -> if (regex.matches(messageTag)) indent else null }
            return indent + this
        }

        companion object Creator {

            private val identMap = mapOf(
                Regex("(\\[Splinter])") to "",
                Regex("(\\[Apprentice #[0-9]{3}])") to "-- ",
                Regex("(\\[OneShot])") to "-- -- ",
                Regex("(\\[Mirror])") to "-- -- ",
                Regex("(\\[Polling])") to "-- -- ",
                Regex("(\\[Cache])") to "-- -- -- ",
                Regex("(\\[.*])") to "-- -- -- -- ",
            )

            fun info(message: String) = Message(
                level = Lumber.Level.Info, message = message, error = null
            )

            fun warn(message: String) = Message(
                level = Lumber.Level.Warn, message = message, error = null
            )

            fun error(message: String, error: Throwable?) = Message(
                level = Lumber.Level.Error, message = message, error = error
            )

            fun debug(message: String) = Message(
                level = Lumber.Level.Debug, message = message, error = null
            )

            fun verbose(message: String) = Message(
                level = Lumber.Level.Verbose, message = message, error = null
            )

            fun assert(message: String) = Message(
                level = Lumber.Level.Assert, message = message, error = null
            )
        }
    }
    //endregion

    /* -------------------------------------------------------------------------------------------*/
    /* Enums -------------------------------------------------------------------------------------*/
    /* -------------------------------------------------------------------------------------------*/
    //region Enums
    enum class ExecutionPolicy {
        ParallelQueue,
        SequentialQueue,
        IgnoreWhenHasRunningOperations, // Default
        CancelWhenHasRunningBeforeStart
    }

    enum class StopPolicy {
        UntilRequest,
        AfterFirstExecution,
        OnLifecycle, // Default
    }
    //endregion
}
