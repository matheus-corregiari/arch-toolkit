@file:Suppress("TooManyFunctions")

package br.com.arch.toolkit.splinter.extension

import br.com.arch.toolkit.splinter.Splinter
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow

// region Info
fun Channel<Splinter.Message>.info(message: String) =
    trySend(Splinter.Message.info(message))

fun MutableSharedFlow<Splinter.Message>.tryInfo(message: String) =
    tryEmit(Splinter.Message.info(message))

suspend fun MutableSharedFlow<Splinter.Message>.info(message: String) =
    emit(Splinter.Message.info(message))
// endregion

// region Error
fun Channel<Splinter.Message>.error(message: String, error: Throwable?) =
    trySend(Splinter.Message.error(message, error))

fun MutableSharedFlow<Splinter.Message>.tryError(message: String, error: Throwable?) =
    tryEmit(Splinter.Message.error(message, error))

suspend fun MutableSharedFlow<Splinter.Message>.error(message: String, error: Throwable?) =
    emit(Splinter.Message.error(message, error))
// endregion

// region Warn
fun Channel<Splinter.Message>.warn(message: String) =
    trySend(Splinter.Message.warn(message))

fun MutableSharedFlow<Splinter.Message>.tryWarn(message: String) =
    tryEmit(Splinter.Message.warn(message))

suspend fun MutableSharedFlow<Splinter.Message>.warn(message: String) =
    emit(Splinter.Message.warn(message))
// endregion

// region Debug
fun Channel<Splinter.Message>.debug(message: String) =
    trySend(Splinter.Message.debug(message))

fun MutableSharedFlow<Splinter.Message>.tryDebug(message: String) =
    tryEmit(Splinter.Message.debug(message))

suspend fun MutableSharedFlow<Splinter.Message>.debug(message: String) =
    emit(Splinter.Message.debug(message))
// endregion

// region Verbose
fun Channel<Splinter.Message>.verbose(message: String) =
    trySend(Splinter.Message.verbose(message))

fun MutableSharedFlow<Splinter.Message>.tryVerbose(message: String) =
    tryEmit(Splinter.Message.verbose(message))

suspend fun MutableSharedFlow<Splinter.Message>.verbose(message: String) =
    emit(Splinter.Message.verbose(message))
// endregion

// region Assert
fun Channel<Splinter.Message>.assert(message: String) =
    trySend(Splinter.Message.assert(message))

fun MutableSharedFlow<Splinter.Message>.tryAssert(message: String) =
    tryEmit(Splinter.Message.assert(message))

suspend fun MutableSharedFlow<Splinter.Message>.assert(message: String) =
    emit(Splinter.Message.assert(message))
// endregion
