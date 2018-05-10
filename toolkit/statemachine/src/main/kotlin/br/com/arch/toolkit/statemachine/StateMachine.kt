package br.com.arch.toolkit.statemachine

import android.os.Bundle

/**
 * Simple State machine implementation.
 *
 * This implementation knows how to handle state changes of a custom State implementation
 *
 * Have fun!
 */
abstract class StateMachine<STATE : StateMachine.State>(val stateCreator: () -> STATE) {

    /**
     * Current state key of the machine
     */
    var currentStateKey = -1
        get() = if (field == -1) config.initialState else field
        private set

    private val stateMap = hashMapOf<Int, STATE>()

    /**
     * State machine Configuration
     */
    val config = Config()
    private var started = false

    /**
     * Implementation for change from one state from another
     *
     * @param state The new State to became Active
     */
    protected abstract fun performChangeState(state: STATE)

    /**
     * Optionally to change the default configuration
     */
    inline fun config(configuration: Config.() -> Unit) = config.run(configuration)

    /**
     * Method to start the machine with the configuration set and/or to restore the state
     *
     * Make sure to call this method after the State machine setup
     *
     * @throws IllegalStateException If the machine is already started
     * @throws IllegalStateException If the machine tries to start with a invalid state
     */
    fun start() {
        if (started) throw IllegalStateException("Machine already started")

        if (currentStateKey > -1) stateMap[currentStateKey]
                ?: throw IllegalStateException("State not found! " +
                        "Make sure to add all states before init the Machine")
        started = true
        if (currentStateKey > -1) {
            changeState(currentStateKey, forceChange = true)
        }
    }

    //region Change Current State
    /**
     * Changes the current state of the state machine
     *
     * @param stateKey The state to become active
     * @param forceChange If true, forces to change to the stateKey param, even if it is already the currentStateKey
     * @param onChangeState Custom implementation for the default onChangeState
     *
     * @throws IllegalStateException If the machine is not started
     * @throws IllegalStateException When it tries to change to a not valid state
     */
    fun changeState(stateKey: Int, forceChange: Boolean = false, onChangeState: ((key: Int) -> Unit)? = config.onChangeState) {
        if (!started) throw IllegalStateException("Call StateMachine\$start() method before make any state changes")

        if (stateKey == currentStateKey && !forceChange) return

        val state = stateMap[stateKey]
                ?: throw IllegalStateException("State $stateKey not exists! Make sure to setup the State Machine before change the states!")

        // On change state
        onChangeState?.invoke(stateKey)

        stateMap[currentStateKey]?.exit?.invoke()
        performChangeState(state)
        state.enter?.invoke()

        currentStateKey = stateKey
    }
    //endregion

    //region Add States
    /**
     * Add a new state
     *
     * @see [StateMachine.state]
     *
     * @throws IllegalStateException If the machine is already started
     * @throws IllegalStateException If the key is < 0
     */
    inline fun state(key: Int, stateConfig: STATE.() -> Unit) {
        state(key, stateCreator.invoke().apply(stateConfig))
    }

    /**
     * Add a new state
     *
     * @see [StateMachine.state]
     *
     * @throws IllegalStateException If the machine is already started
     * @throws IllegalStateException If the key is < 0
     */
    fun state(key: Int, state: STATE) {
        if (started) throw IllegalStateException("Machine already started")

        if (key < 0) throw IllegalStateException("State Keys must be >= 0")
        stateMap[key] = state
    }
    //endregion

    //region Save and restore the Machine state
    /**
     * Restore the state machine state
     *
     * @throws IllegalStateException If the machine is already started
     */
    open fun restoreInstanceState(savedInstanceState: Bundle?) {
        if (started) throw IllegalStateException("Machine already started")

        currentStateKey = savedInstanceState?.getInt("STATE_MACHINE_CURRENT_KEY", currentStateKey) ?: currentStateKey
    }

    /**
     * Save the current state of the state machine
     */
    open fun saveInstanceState() = Bundle().apply {
        putInt("STATE_MACHINE_CURRENT_KEY", currentStateKey)
    }
    //endregion

    //region Config
    class Config internal constructor() {
        var initialState: Int = -1
        var onChangeState: ((key: Int) -> Unit)? = null
    }
    //endregion

    // region State
    open class State {
        var enter: (() -> Unit)? = null
            private set
        var exit: (() -> Unit)? = null
            private set

        fun onEnter(func: () -> Unit) {
            enter = func
        }

        fun onExit(func: () -> Unit) {
            exit = func
        }
    }
    //endregion
}