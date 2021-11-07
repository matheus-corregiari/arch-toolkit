package br.com.arch.toolkit.sample.recycler.adapter

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import br.com.arch.toolkit.delegate.viewProvider
import br.com.arch.toolkit.sample.recycler.adapter.paginating.PaginatingActivity

class MainActivity : AppCompatActivity() {

    private val simpleListButton: Button by viewProvider(R.id.bt_simple_list_example)
    private val multipleViewButton: Button by viewProvider(R.id.bt_multiple_view_types_example)
    private val stickyHeadersButton: Button by viewProvider(R.id.bt_sticky_headers_example)
    private val changingListButton: Button by viewProvider(R.id.bt_changing_list_example)
    private val paginatingListButton: Button by viewProvider(R.id.bt_paginating_list_example)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        simpleListButton.setOnClickListener {
            startActivity(Intent(this, SimpleListActivity::class.java))
        }

        multipleViewButton.setOnClickListener {
            startActivity(Intent(this, MultipleViewTypesActivity::class.java))
        }

        stickyHeadersButton.setOnClickListener {
            startActivity(Intent(this, StickyHeadersActivity::class.java))
        }

        changingListButton.setOnClickListener {
            startActivity(Intent(this, ChangingListActivity::class.java))
        }

        paginatingListButton.setOnClickListener {
            startActivity(Intent(this, PaginatingActivity::class.java))
        }
    }
}
