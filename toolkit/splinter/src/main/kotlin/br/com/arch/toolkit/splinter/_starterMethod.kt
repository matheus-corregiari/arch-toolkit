package br.com.arch.toolkit.splinter

/**
 * Method that creates a simple Splinter instance
 *
 * @param id - Used to identify the logs from this splinter in logcat
 * @param quiet - Used to turn on/off the logs inside logcat
 * @param config - Evil configurations to do your async operation
 *
 * @return A new Splinter
 */
fun <T : Any> splinter(
    id: String = "",
    quiet: Boolean = false,
    config: Splinter<T>.Config.() -> Unit
) = Splinter<T>(id, quiet).config(config)

/**
 * Method that creates Splinter instance and execute it
 *
 * @param id - Used to identify the logs from this splinter in logcat
 * @param quiet - Used to turn on/off the logs inside logcat
 * @param request - The request block to make your request
 *
 * @return A new Splinter
 */
fun <T : Any> oneShotDonatello(
    id: String = "",
    quiet: Boolean = false,
    request: suspend () -> T
) = splinter(id, quiet) { oneShotStrategy { request(request) } }.execute()

/**
 * Method that creates Splinter instance and execute it,
 * returning the LiveData to you for observation
 *
 * @param id - Used to identify the logs from this splinter in logcat
 * @param quiet - Used to turn on/off the logs inside logcat
 * @param request - The request block to make your request
 *
 * @return The ResponseLiveData receiving updates from the Splinter
 */
fun <T : Any> oneShotMichelangelo(
    id: String = "",
    quiet: Boolean = false,
    request: suspend () -> T
) = oneShotDonatello(id, quiet, request).execute().liveData

/**
 * Method that creates Splinter instance and execute it,
 * returning the Flow to you for observation
 *
 * @param id - Used to identify the logs from this splinter in logcat
 * @param quiet - Used to turn on/off the logs inside logcat
 * @param request - The request block to make your request
 *
 * @return The ResponseFlow receiving updates from the Splinter
 */
fun <T : Any> oneShotLeonardo(id: String = "", quiet: Boolean = false, request: suspend () -> T) =
    oneShotDonatello(id, quiet, request).execute().flow
