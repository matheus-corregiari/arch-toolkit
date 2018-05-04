package br.com.arch.toolkit.statemachine

import android.os.Bundle

abstract class StateMachine<STATE : StateMachine.State>(val stateCreator: () -> STATE) {

    var currentStateKey = -1
        get() = if (field == -1) config.initialState else field
        private set

    private val stateMap = hashMapOf<Int, STATE>()
    val config = Config()
    private var started = false

    protected abstract fun performChangeState(state: STATE)

    inline fun config(configuration: Config.() -> Unit) = config.run(configuration)

    fun start() {
        if (currentStateKey > -1) {
            stateMap[currentStateKey]
                    ?: throw IllegalStateException("State not found! " +
                            "Make sure to add all states before init the Machine")
            started = true
            changeState(currentStateKey, forceChange = true)
        }
    }

    //region Change Current State
    fun changeState(stateKey: Int, forceChange: Boolean = false, onChangeState: ((key: Int) -> Unit)? = config.onChangeState) {
        if(!started) throw IllegalStateException("Call StateMachine\$start() method before make any state changes")

        if (stateKey == currentStateKey && !forceChange) return

        val state = stateMap[stateKey]
                ?: throw IllegalStateException("State $stateKey not exists! Make sure to setup the State Machine before change the states!")

        stateMap[currentStateKey]?.exit?.invoke()
        performChangeState(state)
        state.enter?.invoke()

        // On change state
        onChangeState?.invoke(stateKey)

        currentStateKey = stateKey
    }
    //endregion

    //region Add States
    inline fun state(key: Int, stateConfig: STATE.() -> Unit) {
        state(key, stateCreator.invoke().apply(stateConfig))
    }

    fun state(key: Int, state: STATE) {
        if(started) throw IllegalStateException("Machine already started")

        if (key < 0) throw IllegalStateException("State Keys must be >= 0")
        stateMap[key] = state
    }
    //endregion

    //region Save and restore the Machine state
    open fun restoreInstanceState(savedInstanceState: Bundle?) {
        if(started) throw IllegalStateException("Machine already started")

        currentStateKey = savedInstanceState?.getInt("STATE_MACHINE_CURRENT_KEY", currentStateKey) ?: currentStateKey
    }

    open fun saveInstanceState() = Bundle().apply {
        putInt("STATE_MACHINE_CURRENT_KEY", currentStateKey)
        started = false
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