package br.com.arch.toolkit.sample.playground.recyclerAdapter

import android.os.Bundle
import android.widget.LinearLayout.VERTICAL
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.arch.toolkit.delegate.viewProvider
import br.com.arch.toolkit.recyclerAdapter.SimpleAdapter
import br.com.arch.toolkit.sample.playground.R
import br.com.arch.toolkit.sample.playground.recyclerAdapter.itemView.SimpleItemView

class SimpleListActivity : BaseActivity() {

    private val recycler: RecyclerView by viewProvider(R.id.recycler_view)
    private val adapter = SimpleAdapter(::SimpleItemView).withListener(::onItemClick)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.addItemDecoration(DividerItemDecoration(this, VERTICAL))
        recycler.adapter = adapter

        adapter.setList(generateStringList())
    }

    private fun onItemClick(item: String) {
        Toast.makeText(this, item, Toast.LENGTH_SHORT).show()
    }

    private fun generateStringList() = (1 until 20).map { "Item $it" }
}
