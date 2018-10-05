package br.com.arch.toolkit.statemachine

open class TestState : StateMachine.State()

class TestStateMachine : StateMachine<TestState>() {
    override fun performChangeState(state: TestState) {
    }

    override fun newStateInstance(): TestState {
        return TestState()
    }
}