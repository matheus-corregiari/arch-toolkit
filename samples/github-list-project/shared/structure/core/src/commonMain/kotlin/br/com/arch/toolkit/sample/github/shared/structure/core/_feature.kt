@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package br.com.arch.toolkit.sample.github.shared.structure.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.SavedStateHandle
import androidx.savedstate.SavedState
import androidx.savedstate.savedState
import org.jetbrains.compose.resources.StringResource
import org.koin.core.definition.Definition
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope

fun Module.featureRegistry(name: String, definition: Definition<List<FeatureRegistry>>) =
    factory(named(name)) {
        name to definition.invoke(this, parametersOf()).map { it.copy(id = name) }
    }

fun Module.enableSavedStateHandleCompat() =
    single(named("saved-state-compat")) { mutableMapOf<String, SavedState>() }

fun Scope.savedStateHandleCompat(name: String) = runCatching { get<SavedStateHandle>() }.getOrElse {
    val map = getOrNull<MutableMap<String, SavedState>>(named("saved-state-compat"))
    val state = map?.getOrPut(name) { savedState { } }
    SavedStateHandle.createHandle(state, null)
}

data class FeatureRegistry(
    val id: String,
    val version: Int,
    val title: StringResource,
    val description: StringResource,
    val icon: FeatureIcon = FeatureIcon(Icons.Default.Abc),
    val content: List<FeatureContent>
) {
    init {
        require(content.isNotEmpty()) { "Content List must have at least one content to display!" }
    }
}

class FeatureIcon(private val selected: ImageVector, private val unselected: ImageVector) {
    constructor(icon: ImageVector) : this(icon, icon)

    fun getIcon(selected: Boolean = true) = if (selected) this.selected else unselected
}

expect sealed class FeatureContent()
class ComposeContent(val create: @Composable () -> Unit) : FeatureContent()
