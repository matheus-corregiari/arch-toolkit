package br.com.arch.toolkit.sample.recycler.adapter.paginating

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.arch.toolkit.delegate.viewProvider
import br.com.arch.toolkit.recycler.adapter.paginating.Page
import br.com.arch.toolkit.sample.recycler.adapter.BaseActivity
import br.com.arch.toolkit.sample.recycler.adapter.R

class PaginatingActivity : BaseActivity() {

    private val recyclerView: RecyclerView by viewProvider(R.id.recycler_view_paginating)
    private val adapter = SamplePaginatingAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paginating)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.loadMore(::loadMore)
        loadMore(0)
    }

    private fun loadMore(page: Int) {
        val newItems = generateNumbers(page)
        adapter.addPage(Page(
            adapter.items.toMutableList().apply {
                addAll(newItems)
            },
            if(page < 1000/500) page + 1 else null,
            1000
        ))
    }

    private fun generateNumbers(page: Int) : List<String> {
        return List(50) {
            "Number: ${(page * 50) + it}"
        }
    }

}