package br.com.arch.toolkit.sample.github.data.remote.model

class PageDTO<T>(
    val totalCount: Long,
    val incompleteResults: Boolean,
    val items: List<T> = emptyList(),
    var nextPage: Int? = null
)