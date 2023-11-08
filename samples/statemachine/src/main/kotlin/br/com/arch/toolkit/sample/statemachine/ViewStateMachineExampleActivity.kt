package br.com.arch.toolkit.sample.statemachine

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import br.com.arch.toolkit.delegate.viewProvider
import br.com.arch.toolkit.statemachine.ViewStateMachine
import br.com.arch.toolkit.statemachine.config
import br.com.arch.toolkit.statemachine.setup
import br.com.arch.toolkit.statemachine.state

class ViewStateMachineExampleActivity : BaseActivity() {

    private val stateOne: TextView by viewProvider(R.id.state_one)
    private val stateTwo: TextView by viewProvider(R.id.state_two)
    private val stateThree: TextView by viewProvider(R.id.state_three)

    private val btStateOne: Button by viewProvider(R.id.bt_state_one)
    private val btStateTwo: Button by viewProvider(R.id.bt_state_one)
    private val btStateThree: Button by viewProvider(R.id.bt_state_one)

    private val stateMachine = ViewStateMachine()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_state_machine)
        setupStateMachine(savedInstanceState?.getBundle(STATE_MACHINE_RESTORE_KEY))
        setupClickListeners()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBundle(STATE_MACHINE_RESTORE_KEY, stateMachine.saveInstanceState())
    }

    private fun setupClickListeners() {
        btStateOne.setOnClickListener { stateMachine.changeState(STATE_ONE, true) }

        btStateTwo.setOnClickListener {
            stateMachine.changeState(STATE_TWO) { newActiveStateKey ->
                displayToast("Custom Listener: $newActiveStateKey")
            }
        }

        btStateThree.setOnClickListener { stateMachine.changeState(STATE_THREE) }
    }

    private fun setupStateMachine(savedInstanceState: Bundle?) = stateMachine.setup {
        config {
            initialState = STATE_ONE
            setOnChangeState { newActiveStateKey ->
                displayToast("Default Listener: $newActiveStateKey")
            }
        }

        state(STATE_ONE) {
            visibles(stateOne)
            invisibles(stateTwo)
            gones(stateThree)

            onEnter {
                displayToast("State One Is Active")
            }
        }

        state(STATE_TWO) {
            visibles(stateTwo)
            invisibles(stateOne, stateThree)

            onExit {
                displayToast("State Two Is Hidden")
            }
        }

        state(STATE_THREE) {
            visibles(stateThree)
            gones(stateOne, stateTwo)
        }

        restoreInstanceState(savedInstanceState)
    }
}
