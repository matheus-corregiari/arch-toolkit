package br.com.arch.toolkit.statemachine

import br.com.arch.toolkit.statemachine.util.TestState
import br.com.arch.toolkit.statemachine.util.TestStateMachine
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class StateMachineTest {

    @Test(expected = IllegalStateException::class)
    fun whenTryToChangeState_withMachineNotStarted_shouldThrowException() {
        val machine = TestStateMachine()
        Assert.assertFalse(machine.isStarted)

        machine.changeState(0)
    }

    @Test(expected = IllegalStateException::class)
    fun whenAddState_withMachineAlreadyStarted_shouldThrowException() {
        val machine = TestStateMachine()
        machine.start()
        Assert.assertTrue(machine.isStarted)

        machine.addState(0, machine.newStateInstance())
    }

    @Test(expected = IllegalStateException::class)
    fun whenStart_withMachineAlreadyStarted_shouldThrowException() {
        val machine = TestStateMachine()
        machine.start()
        Assert.assertTrue(machine.isStarted)

        machine.start()
    }

    @Test(expected = IllegalStateException::class)
    fun whenRestoreState_withMachineAlreadyStarted_shouldThrowException() {
        val machine = TestStateMachine()
        machine.start()
        Assert.assertTrue(machine.isStarted)

        machine.restoreInstanceState(null)
    }

    @Test(expected = IllegalStateException::class)
    fun startMachine_withoutDefaultStateSet_shouldThrowException() {
        val machine = TestStateMachine()

        machine.addState(0, machine.newStateInstance())
        machine.config { initialState = 1 }

        machine.start()
    }

    @Test(expected = IllegalStateException::class)
    fun changeState_withNotvalidKey_shouldThrowException() {
        val machine = TestStateMachine()

        machine.addState(0, machine.newStateInstance())
        machine.config { initialState = 0 }
        machine.start()

        machine.changeState(1)
    }

    @Test
    fun startMachine_withValidDefaultConfigAndStateConfiguration_shouldChangeMachineStateToInitialState() {
        val machine = TestStateMachine()

        machine.addState(0, machine.newStateInstance())
        machine.config {
            initialState = 0
            setOnChangeState { }
        }
        machine.start()

        Assert.assertEquals(machine.currentStateKey, 0)
        Assert.assertEquals(machine.config.initialState, 0)
        Assert.assertTrue(machine.isStarted)
        Assert.assertNotNull(machine.config.onChangeState)
    }

    @Test
    fun shutdownMachine_shouldResetAllStates() {
        val machine = TestStateMachine().initMachine()
        machine.shutdown()

        Assert.assertEquals(machine.currentStateKey, -1)
        Assert.assertFalse(machine.isStarted)
        Assert.assertEquals(machine.config.initialState, -1)
    }

    @Test
    fun whenChangeState_shouldCallEnter_Exit_andChangeStateCallback() {
        val machine = TestStateMachine()
        val state1 = machine.newStateInstance()
        val state2 = machine.newStateInstance()

        val onEnter = Mockito.mock(StateMachine.State.Callback::class.java)
        val onExit = Mockito.mock(StateMachine.State.Callback::class.java)
        val changeState = Mockito.mock(StateMachine.OnChangeStateCallback::class.java)
        state1.onExit(onExit)
        state2.onEnter(onEnter)

        machine.addState(0, state1)
        machine.addState(1, state2)
        machine.config {
            initialState = 0
            onChangeState = changeState
        }
        machine.start()

        Mockito.verify(changeState).onChangeState(0)

        machine.changeState(1)

        Mockito.verify(onExit).invoke()
        Mockito.verify(onEnter).invoke()
        Mockito.verify(changeState).onChangeState(1)
    }

    @Test
    fun whenForceChangeState_shouldCallEnter_NotCallExit_andCallChangeStateCallback() {
        val machine = TestStateMachine()
        val state1 = machine.newStateInstance()

        val onEnter = Mockito.mock(StateMachine.State.Callback::class.java)
        val onExit = Mockito.mock(StateMachine.State.Callback::class.java)
        val changeState = Mockito.mock(StateMachine.OnChangeStateCallback::class.java)
        state1.onExit(onExit)
        state1.onEnter(onEnter)

        machine.addState(0, state1)
        machine.config {
            initialState = 0
            onChangeState = changeState
        }
        machine.start()

        Mockito.verify(onEnter).invoke()
        Mockito.verify(changeState).onChangeState(0)

        machine.changeState(0, true)

        Mockito.verifyNoInteractions(onExit)
        Mockito.verify(onEnter, times(2)).invoke()
        Mockito.verify(changeState, times(2)).onChangeState(0)
    }

    @Test
    fun whenChangeState_withCustomChangeStateCallback_shouldInvokeTheCustom() {
        val machine = TestStateMachine()
        val state1 = machine.newStateInstance()

        val changeState = Mockito.mock(StateMachine.OnChangeStateCallback::class.java)
        val customChangeState = Mockito.mock(StateMachine.OnChangeStateCallback::class.java)

        machine.addState(0, state1)
        machine.config {
            initialState = 0
            onChangeState = changeState
        }
        machine.start()
        Mockito.verify(changeState).onChangeState(0)

        machine.changeState(0, true, customChangeState)
        machine.changeState(0, customChangeState)

        Mockito.verify(customChangeState).onChangeState(0)
    }

    @Test
    fun saveState_shouldSaveCurrentKey() {
        val machine = TestStateMachine()
        val state1 = machine.newStateInstance()
        val state2 = machine.newStateInstance()

        machine.addState(0, state1)
        machine.addState(1, state2)
        machine.config {
            initialState = 0
        }
        machine.start()
        machine.changeState(1)

        val instanceState = machine.saveInstanceState()
        Assert.assertNotNull(instanceState)
        Assert.assertEquals(1, instanceState.getInt("STATE_MACHINE_CURRENT_KEY"))

        machine.shutdown()

        machine.addState(0, state1)
        machine.addState(1, state2)
        machine.config {
            initialState = 0
        }
        machine.restoreInstanceState(instanceState)
        machine.start()

        Assert.assertEquals(1, machine.currentStateKey)
    }

    @Test
    fun restoreState_passingNull_shouldReturnIfself() {
        val machine = TestStateMachine()
        Assert.assertEquals(machine, machine.restoreInstanceState(null))
    }

    @Test(expected = IllegalStateException::class)
    fun initMachine_withNegativeInitialState_shouldTrowException() {
        val machine = TestStateMachine()
        machine.config {
            initialState = -1
        }
    }

    @Test(expected = IllegalStateException::class)
    fun addState_withNegativeKey_shouldTrowException() {
        val machine = TestStateMachine()
        machine.state(-1) {}
    }

    private fun TestStateMachine.initMachine(): TestStateMachine {
        addState(0, Mockito.mock(TestState::class.java))
        addState(1, Mockito.mock(TestState::class.java))
        config {
            initialState = 0
            setOnChangeState { }
        }
        start()
        return this
    }
}
