package br.com.arch.toolkit.statemachine

/**
 * Auto start Machine
 */
inline fun <STATE : StateMachine.State> StateMachine<STATE>.setup(func: StateMachine<STATE>.() -> Unit) {
    apply(func)
    start()
}

/**
 * Change the default configuration
 */
inline fun <STATE : StateMachine.State> StateMachine<STATE>.config(configuration: StateMachine.Config.() -> Unit) =
    config.run(configuration)

/**
 * Add a new state
 *
 * @throws IllegalStateException If the machine is already started
 * @throws IllegalStateException If the key is < 0
 * @see [StateMachine.addState]
 */
inline fun <STATE : StateMachine.State> StateMachine<STATE>.state(
    key: Int,
    stateConfig: STATE.() -> Unit
) {
    addState(key, newStateInstance().apply(stateConfig))
}
