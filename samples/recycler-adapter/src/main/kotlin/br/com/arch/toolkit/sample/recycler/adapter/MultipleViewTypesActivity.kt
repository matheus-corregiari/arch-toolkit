package br.com.arch.toolkit.sample.recycler.adapter

import android.os.Bundle
import android.widget.LinearLayout.VERTICAL
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.arch.toolkit.delegate.viewProvider
import br.com.arch.toolkit.sample.recycler.adapter.adapter.MultipleViewTypesAdapter

class MultipleViewTypesActivity : BaseActivity() {

    private val recycler by viewProvider<RecyclerView>(R.id.recycler_view)
    private val adapter = MultipleViewTypesAdapter()
        .withListener(::onItemClick)
        .withListener(MultipleViewTypesAdapter.TYPE_TWO, ::onTypeTwoClick)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.addItemDecoration(DividerItemDecoration(this, VERTICAL))
        recycler.adapter = adapter

        adapter.setList(generateStringList())
    }

    private fun onItemClick(item: String) {
        Toast.makeText(this, "Default Listener: $item", Toast.LENGTH_SHORT).show()
    }

    private fun onTypeTwoClick(item: String) {
        Toast.makeText(this, "Type Two: $item", Toast.LENGTH_SHORT).show()
    }

    private fun generateStringList() = (1 until 20).map { "Item $it" }
}
