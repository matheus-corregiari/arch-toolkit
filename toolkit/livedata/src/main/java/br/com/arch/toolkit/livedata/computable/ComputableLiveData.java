package br.com.arch.toolkit.livedata.computable;

import android.arch.lifecycle.LiveData;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

import static br.com.arch.toolkit.livedata.ExecutorUtil.async;

/**
 * Implementation od LiveData to make possible compute something on the WorkerThread, and abort or invalidate if needed
 * <p>
 * Will compute the value only when it has as least one observer, in other words, if the liveData instance is active
 *
 * @param <T> The type of the value do compute
 */
public abstract class ComputableLiveData<T> extends LiveData<T> {

    private final Object lock = new Object();
    private final AtomicBoolean computing = new AtomicBoolean(false);
    private final AtomicBoolean computed = new AtomicBoolean(false);

    @Nullable
    private WeakReference<Thread> lastThread = null;

    /**
     * Method executed on the WorkerThread and responsible for compute some data
     * <p>
     * Please, use the postValue method inside the implementation of this method to post new values into the ComputableLiveData
     */
    @WorkerThread
    abstract void compute();

    /**
     * Method used to abort some ongoing async operation.
     * <p>
     * Example:
     * - You can call the Thread.invalidade() method if you decided, for some reason, start a new thread in your application
     */
    abstract void abort();

    /**
     * @return true if it is computing some value, false otherwise
     */
    public boolean isRunning() {
        return computing.get();
    }

    /**
     * @return true if it has computed some value, false otherwise
     */
    public boolean hasComputed() {
        return computed.get();
    }

    /**
     * @see ComputableLiveData#isRunning()
     * @see ComputableLiveData#hasComputed()
     */
    public boolean isRunningOrHasComputed() {
        return computed.get() || computing.get();
    }

    /**
     * Invalidate the actual data and compute again
     */
    public void invalidate() {
        if (!hasObservers() && computed.get()) computed.set(false);
        else if (!computing.get()) lastThread = new WeakReference<>(async(this::executeRunnable));
    }

    /**
     * Interrupt the ongoing computing Thread and cal
     */
    public void interrupt() {
        if (lastThread != null && lastThread.get() != null) {
            lastThread.get().interrupt();
        }
        abort();
        computing.set(false);
        computed.set(false);
    }

    @Override
    protected void onActive() {
        super.onActive();
        if (!computed.get()) invalidate();
    }

    private void executeRunnable() {
        synchronized (lock) {
            if (computed.get()) return;

            try {
                computing.set(true);
                compute();
                computed.set(true);
            } catch (final Exception error) {
                computed.set(false);
            } finally {
                computing.set(false);
            }
        }
    }
}