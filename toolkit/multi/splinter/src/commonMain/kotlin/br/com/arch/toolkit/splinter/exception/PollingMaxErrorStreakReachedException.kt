package br.com.arch.toolkit.splinter.exception

class PollingMaxErrorStreakReachedException(
    message: String,
    cause: Throwable?
) : IllegalStateException(message, cause)
