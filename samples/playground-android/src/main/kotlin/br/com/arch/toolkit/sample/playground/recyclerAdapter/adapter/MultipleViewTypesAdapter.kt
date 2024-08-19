package br.com.arch.toolkit.sample.playground.recyclerAdapter.adapter

import android.content.Context
import br.com.arch.toolkit.recyclerAdapter.BaseRecyclerAdapter
import br.com.arch.toolkit.recyclerAdapter.ViewBinder
import br.com.arch.toolkit.sample.playground.recyclerAdapter.itemView.AnotherSimpleItemView
import br.com.arch.toolkit.sample.playground.recyclerAdapter.itemView.SimpleItemView

class MultipleViewTypesAdapter : BaseRecyclerAdapter<String>() {

    override fun viewCreator(context: Context, viewType: Int): ViewBinder<*> {
        return when (viewType) {
            TYPE_ONE -> SimpleItemView(context)
            else -> AnotherSimpleItemView(context)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) TYPE_ONE else TYPE_TWO
    }

    companion object {
        const val TYPE_ONE = 0
        const val TYPE_TWO = 1
    }
}
