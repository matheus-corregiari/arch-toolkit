package br.com.arch.toolkit.statemachine

import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.View.INVISIBLE

/**
 * Implementation of [StateMachine]
 * This implementation uses View Visibility to make State transitions
 */
class ViewStateMachine : StateMachine<ViewState>(::ViewState) {

    override fun performChangeState(state: ViewState) = with(state) {
        // Visibility
        gones.forEach { it.visibility = GONE }
        visibles.forEach { it.visibility = VISIBLE }
        invisibles.forEach { it.visibility = INVISIBLE }

        // Enable
        enables.forEach { it.isEnabled = true }
        disables.forEach { it.isEnabled = false }
    }
}