package br.com.arch.toolkit.recycler.adapter

interface ViewBinder<in MODEL> {
    fun bind(model: MODEL)
}