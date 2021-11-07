package br.com.arch.toolkit.recycler.adapter.paginating

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import br.com.arch.toolkit.recycler.adapter.BaseRecyclerAdapter
import br.com.arch.toolkit.recycler.adapter.BaseViewHolder
import br.com.arch.toolkit.recycler.adapter.DefaultItemDiffer
import br.com.arch.toolkit.recycler.adapter.ViewBinder

private const val STATE_TAG = "PaginatingAdapter.StateTag"
private const val TYPE_PROGRESS = 997
private const val TYPE_ERROR = 998
private const val TYPE_EMPTY = 999

abstract class PaginatingAdapter<Model>(
    diff: DiffUtil.ItemCallback<Model> = DefaultItemDiffer()
) : BaseRecyclerAdapter<Model>(diff) {

    private var loadMore: ((Int) -> Unit)? = null
    private var withLoadingItem: Boolean = false
    private var withErrorItem: Boolean = false
    private var withEmptyItem: Boolean = false
    private var nextPage: Int? = null

    protected open val showLoading: Boolean = true
    protected open val showEmpty: Boolean = false
    protected open val showError: Boolean = false

    override fun viewCreator(context: Context, viewType: Int): ViewBinder<*> {
        return when {
            viewType == TYPE_PROGRESS && showLoading ->
                createLoadingItemView(context).apply { tag = STATE_TAG } as ViewBinder<*>
            viewType == TYPE_ERROR && showError ->
                createErrorItemView(context).apply { tag = STATE_TAG } as ViewBinder<*>
            viewType == TYPE_EMPTY && showEmpty ->
                createEmptyItemView(context).apply { tag = STATE_TAG } as ViewBinder<*>
            else -> creatorView(context, viewType)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when(getItemViewType(position)) {
            TYPE_ERROR -> onBindErrorItemView(holder, position)
            TYPE_EMPTY -> onBindEmptyItemView(holder, position)
            TYPE_PROGRESS -> onBindLoadingItemView(holder, position)
            else -> super.onBindViewHolder(holder, position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(position) {
            items.size -> {
                when {
                    withErrorItem -> TYPE_ERROR
                    withEmptyItem -> TYPE_EMPTY
                    else -> TYPE_PROGRESS
                }
            }
            else -> super.getItemViewType(position)
        }
    }

    override fun getItemCount() = super.getItemCount() + if(withLoadingItem) 1 else 0

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.clearOnChildAttachStateChangeListeners()

        val listener = object : RecyclerView.OnChildAttachStateChangeListener {

            private var lastRequestPage = 0

            override fun onChildViewAttachedToWindow(child: View) {
                if(withErrorItem) lastRequestPage = 0
                if(lastRequestPage == nextPage) return
                if(STATE_TAG != child.tag || withErrorItem || withEmptyItem) return
                nextPage?.let {
                    lastRequestPage = it
                    loadMore?.invoke(lastRequestPage)
                }
            }

            override fun onChildViewDetachedFromWindow(child: View) {}
        }

        recyclerView.addOnChildAttachStateChangeListener(listener)
    }

    protected abstract fun creatorView(context: Context, viewType: Int) : ViewBinder<Model>

    protected open fun createErrorItemView(context: Context) : View {
        return View(context)
    }

    protected open fun createLoadingItemView(context: Context) : View {
        return ProgressItemView(context)
    }

    protected open fun createEmptyItemView(context: Context) : View {
        return View(context)
    }

    protected open fun onBindErrorItemView(holder: BaseViewHolder, position: Int) = Unit

    protected open fun onBindLoadingItemView(holder: BaseViewHolder, position: Int) = Unit

    protected open fun onBindEmptyItemView(holder: BaseViewHolder, position: Int) = Unit

    fun loadMore(loadMore: (Int) -> Unit) : PaginatingAdapter<Model> {
        this.loadMore = loadMore
        return this
    }

    fun withPaginatingListener(onItemClick: (Model) -> Unit) : PaginatingAdapter<Model> {
        return withListener(onItemClick) as PaginatingAdapter<Model>
    }

    fun withPaginatingListener(
        itemType: Int,
        onItemClick: (Model) -> Unit
    ) : PaginatingAdapter<Model> {
        return withListener(itemType, onItemClick) as PaginatingAdapter<Model>
    }

    fun addPage(page: Page<Model>) {
        if(nextPage?.equals(page.nextPage) == true) return
        nextPage = page.nextPage
        withErrorItem = false
        withEmptyItem = items.isNotEmpty() && nextPage == null

        if(withLoadingItem && nextPage == null) removeLoadingItem()

        setList(page.items)

        if(withEmptyItem || (!withLoadingItem && nextPage != null)) insertLoadingItem()
    }

    fun addPageLoadError() {
        if(!withLoadingItem) return
        withErrorItem = true
        withEmptyItem = false
        notifyItemChanged(itemCount - 1)
    }

    fun onErrorClick() {
        withErrorItem = false
        withEmptyItem = false
        notifyItemChanged(items.size)
    }

    private fun removeLoadingItem() {
        if(!withLoadingItem) return
        withLoadingItem = false
        runCatching { notifyItemRemoved(itemCount) }
    }

    private fun insertLoadingItem() {
        if(withLoadingItem) return
        withLoadingItem = true
        notifyItemInserted(itemCount - 1)
    }

}