package br.com.arch.toolkit.statemachine

import android.view.View

class ViewState : StateMachine.State() {

    internal val visibles = mutableListOf<View>()
    internal val gones = mutableListOf<View>()
    internal val invisibles = mutableListOf<View>()

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