package br.com.arch.toolkit.stateHandle

import androidx.lifecycle.SavedStateHandle
import androidx.savedstate.SavedState
import androidx.savedstate.savedState
import br.com.arch.toolkit.lumber.Lumber
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope

/**
 * Enables a fallback map-based storage for `SavedStateHandle` inside Koin.
 *
 * Use this when:
 * - A `SavedStateHandle` is **not** available in the current scope (e.g., non-ViewModel injection).
 * - You need to restore or retain lightweight UI state across recreations, but are outside the
 *   AndroidX `viewModel {}` context.
 *
 * This registers an internal state map under `named("saved-state-compat")`.
 * Values are stored per logical key (screen/scope name).
 *
 * ⚠️ This is **compatibility mode**, not the preferred approach.
 * Prefer `viewModel {}` + `SavedStateHandle` when possible.
 */
fun Module.enableSavedStateHandleCompat() =
    single(named("saved-state-compat")) { mutableMapOf<String, SavedState>() }

/**
 * Retrieves a `SavedStateHandle` from the current Koin [Scope], falling back to compatibility mode.
 *
 * Resolution order:
 * 1) If this scope already contains a real `SavedStateHandle`, return it.
 * 2) Otherwise, use the shared compat map (if enabled via [enableSavedStateHandleCompat]).
 * 3) If needed, lazy-create a new `SavedState` entry for the given `name`.
 *
 * This allows scoped state retention **outside** of AndroidX `viewModel` contexts.
 *
 * ## Parameters
 * @param name Logical identifier for the screen/scope. Use something stable and unique.
 *
 * ## Returns
 * A usable `SavedStateHandle` (real or compat-backed).
 *
 * ## Example
 * ```
 * // Koin modules:
 * module {
 *     enableSavedStateHandleCompat()
 * }
 *
 * // In a non-ViewModel scoped screen presenter:
 * class MyPresenter(scope: Scope) {
 *     private val state = scope.savedStateHandleCompat("screen-my-feature")
 *     var query by state.value<String?>("query")
 * }
 * ```
 */
fun Scope.savedStateHandleCompat(name: String) = runCatching { get<SavedStateHandle>() }
    .onFailure {
        Lumber.tag("StateHandleCompat")
            .error(message = "Error initializing SavedStateHandle", error = it)
    }.getOrElse {
        val map = getOrNull<MutableMap<String, SavedState>>(named("saved-state-compat"))
        val state = map?.getOrPut(name) { savedState { } }
        SavedStateHandle.createHandle(state, null)
    }
