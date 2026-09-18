package br.com.arch.toolkit.sample.github.shared.structure.repository

import br.com.arch.toolkit.sample.github.shared.structure.core.ComposeContent
import br.com.arch.toolkit.sample.github.shared.structure.core.FeatureRegistry

class FeatureRepository(
    allFeatureMap: Map<String, List<FeatureRegistry>>
) {
    private val allFeatureHomeMap = allFeatureMap.filterKeys { it.endsWith("-home") }
    val homeComposable = allFeatureHomeMap.mapNotNull { (_, registryList) ->
        registryList.asSequence()
            .map { it.copy(content = it.content.filterIsInstance<ComposeContent>()) }
            .filter { it.content.isNotEmpty() }
            .sortedBy { it.version }.firstOrNull()
    }.distinctBy { it.id }
}
