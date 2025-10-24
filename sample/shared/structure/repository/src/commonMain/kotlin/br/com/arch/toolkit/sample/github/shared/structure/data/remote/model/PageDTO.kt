package br.com.arch.toolkit.sample.github.shared.structure.data.remote.model

import br.com.arch.toolkit.stateHandle.SavableObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class PageDTO(
    @SerialName("total_count") val totalCount: Long,
    @SerialName("incomplete_results") val incompleteResults: Boolean,
    @SerialName("items") val items: List<RepoDTO>,
    @SerialName("next_page") var nextPage: Int? = null
) : SavableObject()
