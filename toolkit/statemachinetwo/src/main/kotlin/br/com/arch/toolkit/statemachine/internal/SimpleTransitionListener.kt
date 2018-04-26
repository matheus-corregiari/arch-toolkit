package br.com.arch.toolkit.statemachine.internal

import android.transition.Transition

internal interface SimpleTransitionListener : Transition.TransitionListener {
    override fun onTransitionEnd(transition: Transition) = Unit

    override fun onTransitionResume(transition: Transition) = Unit

    override fun onTransitionPause(transition: Transition) = Unit

    override fun onTransitionCancel(transition: Transition) = Unit

    override fun onTransitionStart(transition: Transition) = Unit
}