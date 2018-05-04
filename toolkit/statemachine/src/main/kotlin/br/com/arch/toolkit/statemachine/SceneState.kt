package br.com.arch.toolkit.statemachine

import android.transition.Scene
import android.transition.Transition
import android.view.ViewGroup

class SceneState : StateMachine.State() {

    internal var scene: Scene? = null
        private set
    internal var transition: Transition? = null
        private set

    fun scene(pair: Pair<Int, ViewGroup>) {
        scene = Scene.getSceneForLayout(pair.second, pair.first, pair.second.context)
    }

    fun transition(transition: Transition) {
        this.transition = transition
    }
}