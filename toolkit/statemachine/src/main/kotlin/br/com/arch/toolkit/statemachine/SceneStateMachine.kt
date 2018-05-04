package br.com.arch.toolkit.statemachine

import android.transition.TransitionManager.go

/**
 * Implementation of [StateMachine]
 * This implementation uses the Scene framework to make State transitions
 */
class SceneStateMachine : StateMachine<SceneState>(::SceneState) {

    override fun performChangeState(state: SceneState) {
        state.scene?.let {
            val attached = it.sceneRoot?.isAttachedToWindow ?: false
            if (state.transition == null || !attached) {
                it.enter()
            } else {
                go(it, state.transition?.clone())
            }
        }
    }
}
