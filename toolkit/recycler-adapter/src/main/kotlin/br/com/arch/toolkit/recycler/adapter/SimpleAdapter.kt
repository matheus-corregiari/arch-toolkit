package br.com.arch.toolkit.recycler.adapter

import android.content.Context
import android.view.View
import br.com.arch.toolkit.recycler.adapter.stickyheader.StickyHeaderModel

/**
 * Implementation of BaseRecyclerAdapter representing a single viewType
 */
open class SimpleAdapter<MODEL : Any, out VIEW>(private val creator: (context: Context) -> VIEW) :
    BaseRecyclerAdapter<MODEL>()
        where VIEW : View, VIEW : ViewBinder<MODEL> {

    override fun viewCreator(context: Context, viewType: Int) = creator.invoke(context)
}

/**
 * Implementation of BaseRecyclerAdapter representing a single viewType as Header, which will stick
 * to the top of the section while scrolling down, and a single viewType as Item.
 */
open class SimpleStickyAdapter<MODEL, out VIEW, out STICKY_VIEW>(
    private val itemCreator: (context: Context) -> VIEW,
    private val stickyItemCreator: (context: Context) -> STICKY_VIEW
) : BaseRecyclerAdapter<MODEL>()
        where MODEL : StickyHeaderModel, VIEW : View, VIEW : ViewBinder<MODEL>, STICKY_VIEW : View, STICKY_VIEW : ViewBinder<MODEL> {

    override fun viewCreator(context: Context, viewType: Int): ViewBinder<MODEL> =
        if (viewType == STICKY_TYPE) stickyItemCreator.invoke(context) else itemCreator.invoke(
            context
        )

    override fun getItemViewType(position: Int) =
        if (items[position].isSticky) STICKY_TYPE else DEFAULT_TYPE

    override fun isStickyHeader(position: Int) =
        items[position].isSticky

    companion object ViewType {
        const val DEFAULT_TYPE = 0
        const val STICKY_TYPE = 1
    }
}
