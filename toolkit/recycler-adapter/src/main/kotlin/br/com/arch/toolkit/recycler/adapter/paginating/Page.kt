package br.com.arch.toolkit.recycler.adapter.paginating

data class Page<T>(
    var items: List<T> = emptyList(),
    var nextPage: Int? = null,
    var totalItems: Int
)