package br.com.arch.toolkit.statemachine

import android.view.View

class ViewState {
    internal val visibles = mutableListOf<View>()
    internal val gones = mutableListOf<View>()
    internal val invisibles = mutableListOf<View>()
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

    fun visibles(vararg views: View) {
        visibles += views
    }

    fun invisibles(vararg views: View) {
        invisibles += views
    }

    fun gones(vararg views: View) {
        gones += views
    }
}