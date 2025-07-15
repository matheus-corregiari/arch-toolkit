package br.com.arch.toolkit.splinter.cache

import androidx.annotation.WorkerThread
import br.com.arch.toolkit.splinter.DataSetter
import br.com.arch.toolkit.splinter.TargetRegularHolder
import br.com.arch.toolkit.splinter.TargetRegularHolderImpl

/**
 *
 */
sealed class CacheStrategy<T>(
    val id: String,
    internal val holder: TargetRegularHolderImpl<T> = TargetRegularHolderImpl(),
    internal val setter: DataSetter<T?> = holder.setter()
) : TargetRegularHolder<T> by holder {

    internal abstract val localData: T?
    internal abstract val localVersion: DataVersion?

    @WorkerThread
    internal abstract suspend fun save(version: DataVersion, data: T)

    @WorkerThread
    internal abstract suspend fun delete()

    @WorkerThread
    internal abstract suspend fun isLocalDisplayable(version: DataVersion, data: T): Boolean

    @WorkerThread
    internal abstract suspend fun isLocalValid(version: DataVersion, data: T): Boolean

    @WorkerThread
    internal abstract suspend fun newVersion(): DataVersion?

    @WorkerThread
    internal suspend fun update(version: DataVersion?, data: T?) {
        if (version != null && data != null) {
            save(version, data)
        } else {
            delete()
        }
        setter.set(data)
    }

    @WorkerThread
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
