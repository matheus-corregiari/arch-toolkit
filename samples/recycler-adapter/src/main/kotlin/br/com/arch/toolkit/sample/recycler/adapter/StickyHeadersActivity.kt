package br.com.arch.toolkit.sample.recycler.adapter

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import br.com.arch.toolkit.delegate.viewProvider
import br.com.arch.toolkit.recycler.adapter.SimpleStickyAdapter
import br.com.arch.toolkit.recycler.adapter.stickyheader.StickyHeaderModel
import br.com.arch.toolkit.recycler.adapter.stickyheader.StickyHeadersLinearLayoutManager
import br.com.arch.toolkit.sample.recycler.adapter.itemView.StickyItemView

class StickyHeadersActivity : BaseActivity() {

    private val recycler by viewProvider<RecyclerView>(R.id.recycler_view)
    private val adapter = SimpleStickyAdapter(StickyItemView::Item, StickyItemView::Header)
        .withListener(::onItemClick)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        recycler.layoutManager = StickyHeadersLinearLayoutManager<SimpleStickyAdapter<StickyHeaderModelExample, StickyItemView.Item, StickyItemView.Header>>(this)
        recycler.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        recycler.adapter = adapter

        adapter.setList(generateList())
    }

    private fun onItemClick(item: StickyHeaderModelExample) {
        Toast.makeText(this, "Default Listener: ${item.title}", Toast.LENGTH_SHORT).show()
    }

    private fun generateList() = (1 until 19).map {
        if (it == 1 || it % 4 == 0) {
            StickyHeaderModelExample("Header $it").apply { isSticky = true }
        } else {
            StickyHeaderModelExample("Item $it")
        }
    }

    class StickyHeaderModelExample(
        val title: String
    ) : StickyHeaderModel {
        override var isSticky = false
    }
}
