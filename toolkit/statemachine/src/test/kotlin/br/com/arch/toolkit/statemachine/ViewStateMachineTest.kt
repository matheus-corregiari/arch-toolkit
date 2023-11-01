package br.com.arch.toolkit.statemachine

import android.view.View
import android.widget.TextView
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ViewStateMachineTest {

    private lateinit var view1: TextView
    private lateinit var view2: TextView
    private lateinit var view3: TextView
    private lateinit var view4: TextView
    private lateinit var view5: TextView

    @Before
    fun setupViews() = with(RuntimeEnvironment.application) {
        view1 = TextView(this)
        view2 = TextView(this)
        view3 = TextView(this)
        view4 = TextView(this)
        view5 = TextView(this)
    }

    @Test
    fun whenChangeState_shouldApplyViewChangesOnEachState() {
        val machine = ViewStateMachine()
        machine.setup {
            state(0) {
                visibles(null, view1)
                invisibles(null, view2)
                gones(null, view3)
                enables(null, view4)
                disables(null, view5)
                onEnter { }
            }
            state(1) {
                visibles(view3)
                invisibles(view1)
                gones(view2)
                enables(view5)
                disables(view4)
                onExit { }
            }
        }

        Assert.assertEquals(View.VISIBLE, view1.visibility)
        Assert.assertEquals(View.VISIBLE, view2.visibility)
        Assert.assertEquals(View.VISIBLE, view3.visibility)
        Assert.assertTrue(view4.isEnabled)
        Assert.assertTrue(view5.isEnabled)

        machine.changeState(0)

        Assert.assertEquals(View.VISIBLE, view1.visibility)
        Assert.assertEquals(View.INVISIBLE, view2.visibility)
        Assert.assertEquals(View.GONE, view3.visibility)
        Assert.assertTrue(view4.isEnabled)
        Assert.assertFalse(view5.isEnabled)

        machine.changeState(1)

        Assert.assertEquals(View.INVISIBLE, view1.visibility)
        Assert.assertEquals(View.GONE, view2.visibility)
        Assert.assertEquals(View.VISIBLE, view3.visibility)
        Assert.assertFalse(view4.isEnabled)
        Assert.assertTrue(view5.isEnabled)
    }
}
