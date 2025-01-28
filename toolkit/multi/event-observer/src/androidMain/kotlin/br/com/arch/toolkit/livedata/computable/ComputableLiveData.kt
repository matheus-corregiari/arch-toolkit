package br.com.arch.toolkit.livedata.computable

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Implementation of [LiveData] to make it possible compute something on the WorkerThread, and abort or invalidate if needed
 *
 *
 * Will compute the value only when it has at least one observer, in other words, if the liveData instance is active
 *
 * @param <T> The type of the value do compute
</T> */
abstract class ComputableLiveData<T> : LiveData<T>() {

    private val lock = Any()
    private val computing = AtomicBoolean(false)
    private val computed = AtomicBoolean(false)
    private var lastThread: WeakReference<Thread?>? = null

    /**
     * Method executed on the WorkerThread and responsible for compute some data
     *
     *
     * Please, use the [LiveData.postValue] method inside the implementation of this method to post new values into the ComputableLiveData
     */
    @WorkerThread
    abstract fun compute()

    /**
     * Method used to abort some ongoing async operation.
     *
     *
     * Example:
     * - You can call the [Thread.interrupt] method if you decided, for some reason, start a new thread in your application
     */
    abstract fun abort()

    /**
     * @return true if it is computing some value, false otherwise
     */
    val isRunning: Boolean
        get() = computing.get()

    /**
     * @return true if it has computed some value, false otherwise
     */
    fun hasComputed() = computed.get()

    /**
     * @see ComputableLiveData.isRunning
     * @see ComputableLiveData.hasComputed
     */
    val isRunningOrHasComputed: Boolean
        get() = computed.get() || computing.get()

    /**
     * Invalidate the actual data and compute again
     */
    fun invalidate() {
        if (!hasObservers() && computed.get()) {
            computed.set(false)
        } else if (!computing.get()) {
            computed.set(false)
            lastThread = WeakReference(runOnNewThread { executeRunnable() })
        }
    }

    /**
     * Interrupt the ongoing computing Thread and call [ComputableLiveData.abort]
     */
    fun interrupt() {
        if (lastThread != null && lastThread!!.get() != null) {
            lastThread!!.get()!!.interrupt()
        }
        abort()
        computing.set(false)
        computed.set(false)
    }

    override fun onActive() {
        super.onActive()
        if (!computed.get()) invalidate()
    }

    private fun executeRunnable() {
        synchronized(lock) {
            if (computed.get()) return
            runCatching {
                computing.set(true)
                compute()
                computed.set(true)
            }.onFailure {
                computed.set(false)
            }
            computing.set(false)
        }
    }

    /**
     * Execute a Async Thread block
     *
     * @param block The block containing the instructions to make on the WorkerThreat
     * @return The executing Thread instance
     */
    private fun runOnNewThread(block: Runnable): Thread {
        val thread = Thread(block)
        thread.start()
        return thread
    }
}
