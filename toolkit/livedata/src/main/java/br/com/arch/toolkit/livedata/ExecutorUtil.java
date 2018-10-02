package br.com.arch.toolkit.livedata;

import android.support.annotation.NonNull;

public final class ExecutorUtil {

    private ExecutorUtil() {
        // Do nothing
    }

    /**
     * Execute a Async Thread block
     *
     * @param block The block containing the instructions to make on the WorkerThreat
     * @return The executing Thread instance
     */
    @NonNull
    public static Thread async(@NonNull final Runnable block) {
        final Thread thread = new Thread(block);
        thread.start();
        return thread;
    }
}