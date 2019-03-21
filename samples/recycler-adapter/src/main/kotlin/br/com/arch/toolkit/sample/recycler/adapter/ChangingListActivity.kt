package br.com.arch.toolkit.sample.recycler.adapter

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout.VERTICAL
import br.com.arch.toolkit.sample.recycler.adapter.adapter.MultipleViewTypesAdapter

class ChangingListActivity : BaseActivity() {

    private lateinit var recycler: androidx.recyclerview.widget.RecyclerView
    private lateinit var restore: View
    private lateinit var sortDescending: View

    private val adapter = MultipleViewTypesAdapter()
            .withListener(::onItemClick)
            .withListener(MultipleViewTypesAdapter.TYPE_TWO, ::onTypeTwoClick)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sorting_list)
        recycler = findViewById(R.id.recycler_view)
        restore = findViewById(R.id.restore)
        sortDescending = findViewById(R.id.sort_descending)

        recycler.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recycler.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(this, VERTICAL))
        recycler.adapter = adapter

        adapter.setList(generateStringList())

        restore.setOnClickListener {
            adapter.setList(generateStringList())
        }

        sortDescending.setOnClickListener {
            adapter.setList(generateSortedStringList())
        }
    }

    private fun onItemClick(item: String) {
        adapter.addItem(item + System.currentTimeMillis())
    }

    private fun onTypeTwoClick(item: String) {
        adapter.removeItem(item)
    }

    private fun generateStringList() = (1 until 20).map { "Item $it" }
    private fun generateSortedStringList() = generateStringList().minus("Item 2").sortedDescending()
}
