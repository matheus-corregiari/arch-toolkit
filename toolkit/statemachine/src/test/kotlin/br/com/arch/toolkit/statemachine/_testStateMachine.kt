package br.com.arch.toolkit.statemachine

open class TestState : StateMachine.State()

class TestStateMachine : StateMachine<TestState>() {
    override fun performChangeState(state: TestState) = Unit
    override fun newStateInstance() = TestState()
}
