package br.com.arch.toolkit.statemachine

import android.view.View

/**
 * Implementation of [StateMachine]
 * This implementation uses View Visibility to make State transitions
 */
class ViewStateMachine : StateMachine<ViewState>(::ViewState) {

    override fun performChangeState(state: ViewState) = with(state) {
        // Visibility
        gones.forEach { it.visibility = View.GONE }
        visibles.forEach { it.visibility = View.VISIBLE }
        invisibles.forEach { it.visibility = View.INVISIBLE }

        // Enable
        enables.forEach { it.isEnabled = true }
        disables.forEach { it.isEnabled = false }
    }
}