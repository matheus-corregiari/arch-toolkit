package br.com.arch.toolkit.statemachine;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.SparseArray;

/**
 * Simple State machine implementation.
 * <p>
 * This implementation knows how to handle state changes of a custom State implementation
 * <p>
 * Have fun!
 */
public abstract class StateMachine<STATE extends StateMachine.State> {

    /**
     * Current state key of the machine
     */
    private int currentStateKey = -1;

    /**
     * Control the states configurations
     */
    private final SparseArray<STATE> stateMap = new SparseArray<>();

    /**
     * State machine Configuration
     */
    private final Config config = new Config();

    /**
     * Indicates if the machine is started or not
     */
    private boolean started = false;

    /**
     * Implementation for change from one state from another
     *
     * @param state The new State to became Active
     */
    protected abstract void performChangeState(@NonNull final STATE state);

    /**
     * @return A new State instance
     */
    @NonNull
    public abstract STATE newStateInstance();

    /**
     * @return The current configuration of the Machine
     */
    public final Config getConfig() {
        return config;
    }

    /**
     * @return The current state key
     */
    public final int getCurrentStateKey() {
        return currentStateKey == -1 ? config.initialState : currentStateKey;
    }

    /**
     * Method to start the machine with the configuration set and/or to restore the state
     * <p>
     * Make sure to call this method after the State machine setup
     *
     * @throws IllegalStateException If the machine is already started
     * @throws IllegalStateException If the machine tries to start with a invalid state
     */
    public final void start() {
        if (started) throw new IllegalStateException("Machine already started");

        started = true;

        if (getCurrentStateKey() <= -1) return;

        final STATE state = stateMap.get(getCurrentStateKey());
        if (state == null) throw new IllegalStateException("State not found! " +
                "Make sure to add all states before init the Machine");
        changeState(getCurrentStateKey(), true);
    }

    /**
     * @return true if the Machine is started, otherwise, false
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * Reset the machine.
     * <p>
     * Remote the current state reference
     * Restore configurations to default
     * Set the started to false
     * Clear the states map
     * <p>
     * Call when your view/activity/fragment is not available anymore
     */
    public void shutdown() {
        stateMap.clear();
        started = false;
        currentStateKey = -1;
        config.reset();
    }

    //region Change Current State

    /**
     * Changes the current state of the state machine
     *
     * @param stateKey      The state to become active
     * @param forceChange   If true, forces to change to the stateKey param, even if it is already the currentStateKey
     * @param onChangeState Custom implementation for the default onChangeState
     * @throws IllegalStateException If the machine is not started
     * @throws IllegalStateException When it tries to change to a not valid state
     */
    public final void changeState(final int stateKey, final boolean forceChange, @Nullable final OnChangeStateCallback onChangeState) {
        if (!started)
            throw new IllegalStateException("Call StateMachine#start() method before make any state changes");

        if (stateKey == getCurrentStateKey() && !forceChange) return;

        final STATE state = stateMap.get(stateKey);
        final STATE currentState = stateMap.get(getCurrentStateKey());

        if (state == null)
            throw new IllegalStateException("State " + stateKey + " not exists! Make sure to setup the State Machine before change the states!");

        // On change state
        if (onChangeState != null) onChangeState.onChangeState(stateKey);

        if (stateKey != getCurrentStateKey() && currentState != null && currentState.getExit() != null) currentState.getExit().invoke();
        performChangeState(state);
        if (state.getEnter() != null) state.getEnter().invoke();

        currentStateKey = stateKey;
    }

    /**
     * @param stateKey    The state to become active
     * @param forceChange If true, forces to change to the stateKey param, even if it is already the currentStateKey
     * @see StateMachine#changeState(int, boolean, OnChangeStateCallback)
     */
    public final void changeState(final int stateKey, final boolean forceChange) {
        changeState(stateKey, forceChange, config.onChangeState);
    }

    /**
     * @param stateKey      The state to become active
     * @param onChangeState Custom implementation for the default onChangeState
     * @see StateMachine#changeState(int, boolean, OnChangeStateCallback)
     */
    public final void changeState(final int stateKey, @Nullable final OnChangeStateCallback onChangeState) {
        changeState(stateKey, false, onChangeState);
    }

    /**
     * @param stateKey The state to become active
     * @see StateMachine#changeState(int, boolean, OnChangeStateCallback)
     */
    public final void changeState(final int stateKey) {
        changeState(stateKey, false);
    }
    //endregion

    //region Add States

    /**
     * Add a new state
     *
     * @throws IllegalStateException If the machine is already started
     * @throws IllegalStateException If the key is < 0
     */
    public final StateMachine<STATE> addState(final int key, @NonNull final STATE state) {
        if (started) throw new IllegalStateException("Machine already started");

        if (key < 0) throw new IllegalStateException("State Keys must be >= 0");
        stateMap.put(key, state);
        return this;
    }
    //endregion

    //region Save and restore the Machine state

    /**
     * Restore the state machine state
     *
     * @throws IllegalStateException If the machine is already started
     */
    public StateMachine<STATE> restoreInstanceState(@Nullable final Bundle savedInstanceState) {
        if (started) throw new IllegalStateException("Machine already started");
        if (savedInstanceState == null) return this;
        currentStateKey = savedInstanceState.getInt("STATE_MACHINE_CURRENT_KEY", getCurrentStateKey());
        return this;
    }

    /**
     * Save the current state of the state machine
     */
    @NonNull
    public Bundle saveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putInt("STATE_MACHINE_CURRENT_KEY", getCurrentStateKey());
        return bundle;
    }
    //endregion

    //region Config

    /**
     * Holds the Machine configuration
     */
    public static final class Config {

        private Config() {
        }

        private int initialState = -1;

        private OnChangeStateCallback onChangeState = null;

        public int getInitialState() {
            return initialState;
        }

        public void setInitialState(int initialState) {
            if (initialState < 0) throw new IllegalStateException("initialState cannot be < 0");
            this.initialState = initialState;
        }

        @Nullable
        public OnChangeStateCallback getOnChangeState() {
            return onChangeState;
        }

        public void setOnChangeState(@Nullable final OnChangeStateCallback onChangeState) {
            this.onChangeState = onChangeState;
        }

        private void reset() {
            initialState = -1;
            onChangeState = null;
        }
    }
    //endregion

    //region State
    public abstract static class State {

        @Nullable
        private Callback enter = null;
        @Nullable
        private Callback exit = null;

        public State onEnter(@NonNull final Callback callback) {
            enter = callback;
            return this;
        }

        public State onExit(@NonNull final Callback callback) {
            exit = callback;
            return this;
        }

        @Nullable
        final Callback getEnter() {
            return enter;
        }

        @Nullable
        final Callback getExit() {
            return exit;
        }

        public interface Callback {
            void invoke();
        }
    }
    //endregion

    // region Callback
    public interface OnChangeStateCallback {
        void onChangeState(final int key);
    }
    //endregion
}