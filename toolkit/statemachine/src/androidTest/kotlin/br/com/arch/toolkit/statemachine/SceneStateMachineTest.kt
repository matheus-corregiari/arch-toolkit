package br.com.arch.toolkit.statemachine

import android.content.Intent
import android.transition.Fade
import android.transition.Scene
import android.widget.FrameLayout
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.runner.AndroidJUnit4
import br.com.arch.toolkit.test.TestActivity
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SceneStateMachineTest {

    @Rule
    @JvmField
    val rule = IntentsTestRule(TestActivity::class.java, true, false)

    @Test
    fun whenChangeState_shouldApplyViewChangesOnEachState() {
        lateinit var container: FrameLayout
        rule.launchActivity(Intent())
        rule.activity.setView {
            container = FrameLayout(it)
            container
        }
        Thread.sleep(300L)

        val machine = SceneStateMachine()
        machine.setup {
            state(0) {
                scene(android.R.layout.activity_list_item to container)
                onEnter { }
            }
            state(1) {
                scene(android.R.layout.browser_link_context_header to container)
                transition(Fade())
                onExit { }
            }
            state(3) {
                scene(Scene(container))
            }
        }

        rule.activity.runOnUiThread {
            machine.changeState(0)
        }
        Thread.sleep(1000L)

        Assert.assertEquals(0, machine.currentStateKey)
        Assert.assertNotNull(rule.activity.findViewById(android.R.id.icon))
        Assert.assertNull(rule.activity.findViewById(android.R.id.title))

        rule.activity.runOnUiThread {
            machine.changeState(1)
        }
        Thread.sleep(1000L)

        Assert.assertEquals(1, machine.currentStateKey)
        Assert.assertNotNull(rule.activity.findViewById(android.R.id.title))
        Assert.assertNull(rule.activity.findViewById(android.R.id.icon))
    }

    @Test
    fun withoutScene_shouldDoNothing() {

        val machine = SceneStateMachine()
        machine.setup {
            state(0) {
                onEnter { }
            }
            state(1) {
                onExit { }
            }
            state(2) {}
        }

        machine.changeState(0)
        Assert.assertEquals(0, machine.currentStateKey)
        machine.changeState(1)
        Assert.assertEquals(1, machine.currentStateKey)
        machine.changeState(2)
        Assert.assertEquals(2, machine.currentStateKey)
    }
}
