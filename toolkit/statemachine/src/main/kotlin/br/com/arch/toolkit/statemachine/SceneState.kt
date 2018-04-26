package br.com.arch.toolkit.statemachine

import android.transition.Scene
import android.transition.Transition
import android.view.ViewGroup

class SceneState {

    internal var scene: Scene? = null
        private set
    internal var transition: Transition? = null
        private set
    internal var enter: (() -> Unit)? = null
        private set
    internal var exit: (() -> Unit)? = null
        private set

    fun onEnter(func: () -> Unit) {
        enter = func
    }

    fun onExit(func: () -> Unit) {
        exit = func
    }

    fun scene(pair: Pair<Int, ViewGroup>) {
        scene = Scene.getSceneForLayout(pair.second, pair.first, pair.second.context)
    }

    fun transition(transition: Transition) {
        this.transition = transition
    }
}