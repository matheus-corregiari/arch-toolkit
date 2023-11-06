package br.com.arch.toolkit.foldable.activity

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.PositionAssertions.isCompletelyLeftOf
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.window.testing.layout.WindowLayoutInfoPublisherRule
import br.com.arch.toolkit.foldable.R
import br.com.arch.toolkit.foldable.utils.VerticalFoldableDummyActivity
import br.com.arch.toolkit.foldable.utils.close
import br.com.arch.toolkit.foldable.utils.halfOpen
import br.com.arch.toolkit.foldable.utils.openFlat
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class VerticalFoldableActivityTest {
    private val activityRule = ActivityScenarioRule(VerticalFoldableDummyActivity::class.java)
    private val publisherRule = WindowLayoutInfoPublisherRule()

    @get:Rule
    val testRule: TestRule = RuleChain.outerRule(publisherRule).around(activityRule)

    @Test
    fun whenClosed_givenVerticalOrientation_shouldHideEndView() {
        activityRule.scenario.onActivity { publisherRule.close() }
        onView(withId(R.id.end_layout)).check(matches(not(isDisplayed())))
    }

    @Test
    fun whenFullyOpen_givenVerticalOrientation_shouldShowStartViewLeftOfEndView() {
        activityRule.scenario.onActivity { publisherRule.openFlat(it) }
        onView(withId(R.id.start_layout)).check(isCompletelyLeftOf(withId(R.id.end_layout)))
    }

    @Test
    fun whenHalfOpen_givenVerticalOrientation_shouldShowStartViewLeftOfEndView() {
        activityRule.scenario.onActivity { publisherRule.halfOpen(it) }
        onView(withId(R.id.start_layout)).check(isCompletelyLeftOf(withId(R.id.end_layout)))
    }
}
