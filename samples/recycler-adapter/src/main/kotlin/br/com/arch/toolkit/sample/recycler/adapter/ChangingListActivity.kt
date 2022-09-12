package br.com.arch.toolkit.sample.recycler.adapter

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout.VERTICAL
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.arch.toolkit.delegate.viewProvider
import br.com.arch.toolkit.sample.recycler.adapter.adapter.MultipleViewTypesAdapter

class ChangingListActivity : BaseActivity() {

    private val recycler: RecyclerView by viewProvider(R.id.recycler_view)
    private val restore: View by viewProvider(R.id.restore)
    private val sortDescending: View by viewProvider(R.id.sort_descending)

    private val adapter = MultipleViewTypesAdapter()
        .withListener(::onItemClick)
        .withListener(MultipleViewTypesAdapter.TYPE_TWO, ::onTypeTwoClick)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sorting_list)

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.addItemDecoration(DividerItemDecoration(this, VERTICAL))
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
