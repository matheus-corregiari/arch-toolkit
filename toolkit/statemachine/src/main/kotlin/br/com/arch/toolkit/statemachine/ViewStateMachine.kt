package br.com.arch.toolkit.statemachine

import android.view.View.*

/**
 * Implementation of [StateMachine]
 * This implementation uses View Visibility to make State transitions
 */
class ViewStateMachine : StateMachine<ViewState>(::ViewState) {

    override fun performChangeState(state: ViewState) = with(state) {
        gones.forEach { it.visibility = GONE }
        visibles.forEach { it.visibility = VISIBLE }
        invisibles.forEach { it.visibility = INVISIBLE }
    }
}