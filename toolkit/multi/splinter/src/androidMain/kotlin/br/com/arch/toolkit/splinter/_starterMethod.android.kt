package br.com.arch.toolkit.splinter

import br.com.arch.toolkit.splinter.strategy.OneShot

/**
 * Method that creates Splinter instance and execute it,
 * returning the LiveData to you for observation
 *
 * @param id - Used to identify the logs from this splinter in logcat
 * @param request - The request block to make your request
 *
 * @return The ResponseLiveData receiving updates from the Splinter
 */
fun <T> splinterExecuteRequestLiveData(
    id: String,
    config: Splinter.Config.Builder<T>.() -> Unit = {},
    request: suspend OneShot.Context<T>.() -> T,
) = splinterExecuteRequest(
    id = id,
    config = config,
    request = request
).liveData
