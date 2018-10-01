package br.com.arch.toolkit.livedata

import android.arch.lifecycle.LiveData
import android.support.annotation.WorkerThread
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean

abstract class ComputableLiveData<T> : LiveData<T>() {

    private val lock = Object()

    private val computing = AtomicBoolean(false)
    private val computed = AtomicBoolean(false)
    private var lastThread: WeakReference<Thread>? = null

    val running get() = computing.get()
    val isComputed get() = computed.get()

    val runningOrComputed get() = computed.get() || computing.get()

    @WorkerThread
    protected abstract fun compute()

    protected abstract fun abort()

    open fun invalidate() {
        if (!hasObservers() && computed.get()) computed.set(false)
        else if (!computing.get()) lastThread = WeakReference(async(::executeRunnable))
    }

    open fun interrupt() {
        lastThread?.get()?.interrupt()
        abort()
        computing.set(false)
        computed.set(false)
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