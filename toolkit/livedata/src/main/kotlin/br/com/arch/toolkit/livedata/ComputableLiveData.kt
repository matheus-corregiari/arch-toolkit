package br.com.arch.toolkit.livedata

import android.support.annotation.WorkerThread
import java.util.concurrent.atomic.AtomicBoolean

abstract class ComputableLiveData<T> : InterceptableLiveData<T>() {

    private val lock = Object()

    private val computing = AtomicBoolean(false)
    private val computed = AtomicBoolean(false)

    val running get() = computing.get()
    val isComputed get() = computed.get()

    val runningOrComputed get() = computed.get() || computing.get()

    @WorkerThread
    protected abstract fun compute()

    open fun invalidate() {
        if (!hasObservers() && computed.get()) computed.set(false)
        else if (!computing.get()) async(this::executeRunnable)
    }

    override fun onActive() {
        super.onActive()
        if (!computed.get()) invalidate()
    }

    private fun executeRunnable() = synchronized(lock) {
        if (computed.get()) return@synchronized

        try {
            computing.set(true)
            compute()
            computed.set(true)
        } catch (error: Exception) {
            computed.set(false)
        } finally {
            computing.set(false)
        }
    }

}