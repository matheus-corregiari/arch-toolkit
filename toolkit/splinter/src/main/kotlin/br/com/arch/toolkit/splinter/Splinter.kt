package br.com.arch.toolkit.splinter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import br.com.arch.toolkit.flow.MutableResponseFlow
import br.com.arch.toolkit.flow.ResponseFlow
import br.com.arch.toolkit.livedata.MutableResponseLiveData
import br.com.arch.toolkit.livedata.ResponseLiveData
import br.com.arch.toolkit.result.DataResultStatus
import br.com.arch.toolkit.splinter.extension.invokeCatching
import br.com.arch.toolkit.splinter.strategy.OneShot
import br.com.arch.toolkit.splinter.strategy.Strategy
import br.com.arch.toolkit.util.dataResultNone
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Evil class that knows how to handle events asynchronously
 *
 * It uses a DataResult to wrap the responses and tells the observer what is going on inside this evil class
 */
class Splinter<RETURN : Any> internal constructor(
    private val id: String,
    private val quiet: Boolean
) : DefaultLifecycleObserver {

    //region Return Types
    /**
     * Flow that streams all data emitted from this instance of Splinter
     */
    private val _flow = MutableResponseFlow<RETURN>()
    val flow: ResponseFlow<RETURN>
        get() = _flow.shareIn(
            scope = config.scope,
            started = SharingStarted.WhileSubscribed(),
        )

    /**
     * LiveData that streams all data emitted from this instance of Splinter
     */
    private val _liveData = MutableResponseLiveData<RETURN>()
    val liveData: ResponseLiveData<RETURN> get() = _liveData

    /**
     * Last emitted DataResult
     */
    fun get() = _flow.value

    /**
     * Last emitted Data inside the last emitted DataResult
     */
    val data: RETURN? get() = get().data

    /**
     * Last emitted Throwable inside the last emitted DataResult
     */
    val error: Throwable? get() = get().error

    /**
     * Last emitted Status inside the last emitted DataResult
     */
    val status: DataResultStatus get() = get().status
    //endregion

    //region Jobs, Coroutines and Locks
    private val lock = Object()
    private val operationLock = Object()
    private val config = Config()
    private val supervisorJob = SupervisorJob()
    private var job: Job = Job().apply(CompletableJob::complete)
    private var onCancel: (() -> Unit)? = null
    //endregion

    /**
     * Flag that indicates if this Splinter is running or not!
     */
    val isRunning: Boolean get() = supervisorJob.isActive && job.isActive

    /**
     * Executed when this running splinter gets canceled
     *
     * @See Splinter.cancel
     */
    fun onCancel(func: () -> Unit) = apply { onCancel = func }

    /**
     * Block that configure how this splinter will work ^^
     *
     * @see Splinter.Config
     */
    fun config(config: Config.() -> Unit) = apply { this.config.run(config) }

    /**
     * Execute with all configurations using the execution policy and strategy
     *
     * @see Splinter.ExecutionPolicy
     * @see Strategy
     */
    fun execute(): Splinter<RETURN> = synchronized(lock) {
        when (config.policy) {
            /**
             * When the code enter here, means that we need to check
             * if the job is already running
             *
             * If it is running, we do nothing and return
             *
             * @see ExecutionPolicy
             **/
            ExecutionPolicy.WAIT_IF_RUNNING -> {
                if (isRunning) {
                    logInfo("[Execute] Already running, let's enjoy the same running operation!")
                    return@synchronized this
                }
            }

            /**
             * When the code enter here, means that we need to check
             * if the job is already running
             *
             * If it is running, we must cancel it and let the method
             * continue to create a new job
             *
             * @see ExecutionPolicy
             **/
            ExecutionPolicy.CANCEL_RUNNING_AND_RESTART -> if (isRunning) {
                logInfo("[Execute] Oh no! It's running! Let's cancel it and start again!")
                reset()
            }
        }

        if (isRunning.not()) {
            if (job.isActive || job.isCompleted || job.isCancelled) {
                logInfo("[Execute] Creating a new job!")
                job = newJob()
            }
            if (job.start()) {
                /* This means that the job has been started with success ^^ */
            } else {
                logInfo("[Execute] Unable to start job, let's try again")
                return@synchronized execute()
            }
        } else {
            logInfo("[Execute] Unable to complete execution, let's try again")
            return@synchronized execute()
        }

        return@synchronized this
    }

    /**
     * Reset the value inside the observables to the initial state
     */
    fun reset() {
        logWarning("[Reset] Reset!")
        if (isRunning) {
            cancel()
        }
        _flow.value = dataResultNone()
        _liveData.value = dataResultNone()
    }

    /**
     * Cancel running execution
     *
     * Warning: This will throw a message with status ERROR
     */
    fun cancel() = kotlin.runCatching {
        if (job.isActive || job.isCancelled.not()) {
            job.cancel("Cancel operation id: $id")
            logWarning("[Cancel] Canceled with success!")
            onCancel?.invokeCatching()
        }
    }.onFailure {
        logWarning("[Cancel] Cancel failed!", it)
    }.getOrDefault(Unit)

    /**
     * Lifecycle callback to automatically stop this operation if the observed lifecycle goes away
     *
     * This only works if you set the lifecycle owner inside the configuration block ;)
     */
    override fun onDestroy(owner: LifecycleOwner) {
        if (isRunning && status == DataResultStatus.LOADING) {
            logWarning("[Cancel] Canceling job using lifecycle callback onDestroy!")
            cancel()
        }
    }

    /**
     * Log level INFO
     */
    internal fun logInfo(message: String, error: Throwable? = null) =
        if (quiet) {
            Unit
        } else {
            Timber.tag(if (id.isBlank()) "Splinter" else "Splinter[$id]")
                .i(error, message)
        }

    /**
     * Log level ERROR
     */
    internal fun logError(message: String, error: Throwable? = null) =
        if (quiet) {
            Unit
        } else {
            Timber.tag(if (id.isBlank()) "Splinter" else "Splinter[$id]")
                .e(error, message)
        }

    /**
     * Log level WARN
     */
    internal fun logWarning(message: String, error: Throwable? = null) =
        if (quiet) {
            Unit
        } else {
            Timber.tag(if (id.isBlank()) "Splinter" else "Splinter[$id]")
                .w(error, message)
        }

    /**
     * Creates a new job to create a new operation from scratch!
     */
    private fun newJob(): Job = config.scope.launch(start = CoroutineStart.LAZY) {
        kotlin.runCatching {
            logInfo("[Job] Job started!")
            flow {
                logInfo("[Job] Flow started!")
                if (config.policy == ExecutionPolicy.WAIT_IF_RUNNING) {
                    synchronized(operationLock) { /* - */ }
                }

                requireNotNull(config.strategy) { "You Must set a strategy to run" }
                    .execute(this, this@Splinter)
            }.catch { flowFailure ->
                logError("[Job] Something went wrong inside the operation", flowFailure)
                if (status != DataResultStatus.SUCCESS) {
                    requireNotNull(config.strategy) { "You Must set a strategy to run" }.flowError(
                        flowFailure,
                        this,
                        this@Splinter
                    )
                }
            }.onEach(_liveData::postValue).collect { _flow.emit(it) }
        }.onFailure { majorFailure ->
            logError("[Job] Major failure inside operation!", majorFailure)
            if (status != DataResultStatus.SUCCESS) {
                flow {
                    requireNotNull(config.strategy) { "You Must set a strategy to run" }.majorError(
                        majorFailure,
                        this,
                        this@Splinter
                    )
                }.onEach(_liveData::postValue).collect { _flow.emit(it) }
            }
        }
        logInfo("[Job] Finished!")
    }

    /**
     * This hold every single configuration inside splinter!
     */
    inner class Config internal constructor() {

        //region Scope
        /**
         * Scope to define where we are running this operation inside the coroutine
         *
         * The default will be a regular CoroutineScope with the IO Dispatcher <3
         */
        internal var scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
            private set

        fun scope(scope: CoroutineScope) = apply { this.scope = scope }

        //endregion

        //region LifecycleOwner
        /**
         * The owner to observe
         *
         * If you set this configuration, the splinter will add himself as observer and automatically
         * stop the operation (if running) when the lifecycle get destroyed
         */
        internal var lifecycleOwner: LifecycleOwner? = null
            private set

        fun owner(owner: LifecycleOwner) = apply {
            this.lifecycleOwner?.lifecycle?.removeObserver(this@Splinter)

            this.lifecycleOwner = owner

            owner.lifecycle.removeObserver(this@Splinter)
            owner.lifecycle.addObserver(this@Splinter)
        }
        //endregion

        //region Strategy
        /**
         * Tells splinter how to execute the operation!
         *
         * Basically, Splinter knows how to emit result data inside a LiveData or a Flow
         * This Strategy tells him HOW to emit
         *
         * Can be a OneShot... a Polling... a Stream... Ho knows hehehe
         */
        internal var strategy: Strategy<RETURN>? = null
            private set

        inline fun strategy(crossinline strategy: () -> Strategy<RETURN>) =
            strategy(strategy.invoke())

        fun strategy(strategy: Strategy<RETURN>) = apply {
            this.strategy = strategy
        }

        /**
         * Define and configure a OneShot strategy to this splinter
         */
        fun oneShotStrategy(strategyConfig: OneShot<RETURN>.Config.() -> Unit) = apply {
            this.strategy = Strategy.oneShot(strategyConfig)
        }
        //endregion

        //region Execution Policy
        /**
         * This instance is really evil in form of a Enum!
         *
         * This tells this splinter how it should proceed in case something calls the execute method
         * in case this splinter is already running \o/
         */
        internal var policy: ExecutionPolicy = ExecutionPolicy.CANCEL_RUNNING_AND_RESTART
            private set

        fun policy(policy: ExecutionPolicy) = apply {
            this.policy = policy
        }
        //endregion
    }

    /**
     * This policy enum class serve only for one thing!
     *
     * Tells the Splinter instance how to behave when you press the Execution button(method HUEHUE)
     */
    enum class ExecutionPolicy {

        /**
         * When something trigger the execute() method, if this instance is already running
         * it will just return the splinter and let the river flow like always
         */
        WAIT_IF_RUNNING,

        /**
         * When something trigger the execute() method, if this instance is already running
         * it will cancel the running job and restart from the beginning
         */
        CANCEL_RUNNING_AND_RESTART // Default
    }
}
