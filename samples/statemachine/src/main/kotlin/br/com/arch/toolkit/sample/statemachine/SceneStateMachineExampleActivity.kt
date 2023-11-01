package br.com.arch.toolkit.sample.statemachine

import android.os.Bundle
import android.transition.Fade
import android.transition.Slide
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import br.com.arch.toolkit.delegate.viewProvider
import br.com.arch.toolkit.statemachine.SceneStateMachine
import br.com.arch.toolkit.statemachine.config
import br.com.arch.toolkit.statemachine.scene
import br.com.arch.toolkit.statemachine.state

class SceneStateMachineExampleActivity : BaseActivity() {

    private val stateContainer: FrameLayout by viewProvider(R.id.state_container)

    private val btStateOne: Button by viewProvider(R.id.bt_state_one)
    private val btStateTwo: Button by viewProvider(R.id.bt_state_two)
    private val btStateThree: Button by viewProvider(R.id.bt_state_three)

    private val stateMachine = SceneStateMachine()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scene_state_machine)
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
            stateMachine.changeState(STATE_TWO, { newActiveStateKey ->
                displayToast("Custom Listener: $newActiveStateKey")
            })
        }

        btStateThree.setOnClickListener { stateMachine.changeState(STATE_THREE) }
    }

    private fun setupStateMachine(savedInstanceState: Bundle?) = with(stateMachine) {
        restoreInstanceState(savedInstanceState)

        config {
            initialState = STATE_ONE
            setOnChangeState { newActiveStateKey ->
                displayToast("Default Listener: $newActiveStateKey")
            }
        }

        state(STATE_ONE) {
            scene(R.layout.scene_one to stateContainer)

            onEnter {
                displayToast("State One Is Active")
            }
        }

        state(STATE_TWO) {
            scene(R.layout.scene_two to stateContainer)
            transition(Fade())

            onExit {
                displayToast("State Two Is Hidden")
            }
        }

        state(STATE_THREE) {
            scene(R.layout.scene_three to stateContainer)
            transition(Slide(Gravity.START))
        }

        start()
    }
}
