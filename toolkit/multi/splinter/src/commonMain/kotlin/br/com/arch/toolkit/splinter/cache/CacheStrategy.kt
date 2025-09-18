package br.com.arch.toolkit.splinter.cache

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 *
 */
abstract class CacheStrategy<T>(val id: String) {

    internal abstract val localData: T?
    internal abstract val localVersion: DataVersion?

    //region Getters
    private val _flow = MutableStateFlow<T?>(null)
    val flow: Flow<T?> get() = _flow.asSharedFlow()
    fun get(): T? = _flow.value
    //endregion

    internal abstract suspend fun save(version: DataVersion, data: T)

    internal abstract suspend fun delete()

    internal abstract suspend fun isLocalDisplayable(version: DataVersion, data: T): Boolean

    internal abstract suspend fun isLocalValid(version: DataVersion, data: T): Boolean

    internal abstract suspend fun newVersion(): DataVersion?

    internal suspend fun update(version: DataVersion?, data: T?) {
        if (version != null && data != null) {
            save(version, data)
        } else {
            delete()
        }
        _flow.emit(data)
    }

    internal suspend fun howToProceed(remoteVersion: DataVersion?, local: T?) = runCatching {
        when {
            //
            remoteVersion == null -> {
                update(null, null)
                HowToProceed.IGNORE_CACHE
            }

            //
            local != null && isLocalValid(remoteVersion, local) ->
                HowToProceed.STOP_FLOW_AND_DISPATCH_CACHE

            //
            local != null && isLocalDisplayable(remoteVersion, local) ->
                HowToProceed.DISPATCH_CACHE

            //
            else -> {
                update(null, null)
                HowToProceed.IGNORE_CACHE
            }
        }
    }.getOrDefault(HowToProceed.IGNORE_CACHE)

    /**
     *
     */
    internal enum class HowToProceed {
        IGNORE_CACHE,
        STOP_FLOW_AND_DISPATCH_CACHE,
        DISPATCH_CACHE,
    }

    /**
     *
     */
    internal data class DataVersion(
        val version: String,
    )
}
