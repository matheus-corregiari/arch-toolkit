package br.com.arch.toolkit.splinter

import br.com.arch.toolkit.splinter.strategy.OneShot
import br.com.arch.toolkit.splinter.strategy.Strategy

/**
 * Method that creates a simple Splinter instance
 *
 * @param id - Used to identify the logs from this splinter in logcat
 * @param config - Evil configurations to do your async operation
 *
 * @return A new Splinter
 */
fun <T> splinter(
    id: String,
    strategy: Strategy<T>,
    config: Splinter.Config.Builder<T>.() -> Unit = {},
) = Splinter(
    id = id,
    config = Splinter.Config(config),
    strategy = strategy
)

/**
 * Method that creates Splinter instance and execute it
 *
 * @param id - Used to identify the logs from this splinter in logcat
 * @param request - The request block to make your request
 *
 * @return A new Splinter
 */
fun <T> splinterExecuteRequest(
    id: String,
    config: Splinter.Config.Builder<T>.() -> Unit = {},
    request: suspend OneShot.Context<T>.() -> T,
) = splinter(
    id = id,
    config = config,
    strategy = Strategy.oneShot { request(request) }
).execute()

/**
 * Method that creates Splinter instance and execute it,
 * returning the Flow to you for observation
 *
 * @param id - Used to identify the logs from this splinter in logcat
 * @param request - The request block to make your request
 *
 * @return The ResponseFlow receiving updates from the Splinter
 */
fun <T> splinterExecuteRequestFlow(
    id: String,
    config: Splinter.Config.Builder<T>.() -> Unit = {},
    request: suspend OneShot.Context<T>.() -> T,
) = splinterExecuteRequest(
    id = id,
    config = config,
    request = request
).liveFlow
