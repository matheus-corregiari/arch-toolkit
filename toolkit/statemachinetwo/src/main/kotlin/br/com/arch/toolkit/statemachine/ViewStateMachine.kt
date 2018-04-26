package br.com.arch.toolkit.statemachine

import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.View.INVISIBLE

/**
 * Implementation of [StateMachine]
 * This implementation uses View Visibility to make State transitions
 */
class ViewStateMachine : StateMachine<ViewState>() {

    override fun performChangeState(state: ViewState) {
        stateMap[currentStateKey]?.exit?.invoke()
        state.gones.forEach { it.visibility = GONE }
        state.visibles.forEach { it.visibility = VISIBLE }
        state.invisibles.forEach { it.visibility = INVISIBLE }
        state.enter?.invoke()
    }

    override fun createState() = ViewState()
}