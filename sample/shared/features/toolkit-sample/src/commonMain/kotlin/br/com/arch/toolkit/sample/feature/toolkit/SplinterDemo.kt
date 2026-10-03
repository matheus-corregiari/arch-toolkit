package br.com.arch.toolkit.sample.feature.toolkit

import br.com.arch.toolkit.splinter.Splinter
import br.com.arch.toolkit.splinter.splinter
import br.com.arch.toolkit.splinter.strategy.Strategy
import br.com.arch.toolkit.util.dataResultNone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// snippet:timing:start
private const val DEMO_STEP_DELAY_MILLIS = 600L
private const val POLLING_STEPS = 3
// snippet:timing:end

/** Local deterministic tasks: no network or credentials required. */
class SplinterDemo(scope: CoroutineScope) {
    private val mutableRequest = MutableStateFlow(dataResultNone<String>())
    val request = mutableRequest.asStateFlow()
    private val mutablePolling = MutableStateFlow(dataResultNone<String>())
    val polling = mutablePolling.asStateFlow()

    // snippet:oneShot:start
    private var failRequest = false
    private val oneShot = splinter(
        "showcase-request",
        Strategy.oneShot {
            request {
                sendSnapshot("1 / 2")
                delay(DEMO_STEP_DELAY_MILLIS)
                check(!failRequest) { "Demo request failed" }
                sendSnapshot("2 / 2")
                delay(DEMO_STEP_DELAY_MILLIS)
                "Arch Toolkit"
            }
        }
    ) {
        scope(scope)
        stop(Splinter.StopPolicy.UntilRequest)
        logging(false)
        history(data = 1, logs = 20)
    }
    // snippet:oneShot:end

    // snippet:polling:start
    private var pollingStep = 0
    private val poller = splinter(
        "showcase-polling",
        Strategy.polling {
            request { "${++pollingStep} / $POLLING_STEPS" }
            delay(DEMO_STEP_DELAY_MILLIS)
            shouldStop { pollingStep == POLLING_STEPS }
            limitLoopCount(POLLING_STEPS.toLong())
        }
    ) {
        scope(scope)
        stop(Splinter.StopPolicy.UntilRequest)
        logging(false)
        history(data = 1, logs = 20)
    }
    // snippet:polling:end

    init {
        scope.launch { oneShot.resultHolder.fullFlow.collect { mutableRequest.value = it } }
        scope.launch { poller.resultHolder.fullFlow.collect { mutablePolling.value = it } }
    }

    fun load(fail: Boolean = false) {
        if (oneShot.isRunning) return
        failRequest = fail
        oneShot.execute()
    }

    fun poll() {
        if (poller.isRunning) return
        pollingStep = 0
        poller.execute()
    }

    fun cancelRequest() {
        oneShot.cancel()
        mutableRequest.value = dataResultNone()
    }

    fun cancelPolling() {
        poller.cancel()
        mutablePolling.value = dataResultNone()
    }

    fun close() {
        oneShot.kill()
        poller.kill()
    }
}
