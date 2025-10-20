package br.com.arch.toolkit.stateHandle

import androidx.lifecycle.SavedStateHandle
import androidx.savedstate.SavedState
import androidx.savedstate.savedState
import br.com.arch.toolkit.lumber.Lumber
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope

fun Module.enableSavedStateHandleCompat() =
    single(named("saved-state-compat")) { mutableMapOf<String, SavedState>() }

fun Scope.savedStateHandleCompat(name: String) = runCatching { get<SavedStateHandle>() }
    .onFailure {
        Lumber.tag("StateHandleCompat")
            .error(message = "Error initializing SavedStateHandle", error = it)
    }.getOrElse {
        val map = getOrNull<MutableMap<String, SavedState>>(named("saved-state-compat"))
        val state = map?.getOrPut(name) { savedState { } }
        SavedStateHandle.createHandle(state, null)
    }
