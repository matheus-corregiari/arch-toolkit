package br.com.arch.toolkit.sample.recycler.adapter

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.LinearLayout.HORIZONTAL
import android.widget.LinearLayout.VERTICAL
import android.widget.Toast
import br.com.arch.toolkit.recycler.adapter.SimpleAdapter
import br.com.arch.toolkit.sample.recycler.adapter.itemView.SimpleItemView

class SimpleListActivity : BaseActivity() {

    private lateinit var recycler: RecyclerView
    private val adapter = SimpleAdapter(::SimpleItemView).withListener(::onItemClick)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        recycler = findViewById(R.id.recycler_view)
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
