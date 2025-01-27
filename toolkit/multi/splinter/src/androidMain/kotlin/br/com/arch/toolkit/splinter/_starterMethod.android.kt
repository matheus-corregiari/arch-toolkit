package br.com.arch.toolkit.splinter

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
fun <T : Any> splinterLiveData(
    id: String = "",
    quiet: Boolean = false,
    request: suspend () -> T
) = executeSplinter(id, quiet, request).liveData
