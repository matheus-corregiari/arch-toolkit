package br.com.arch.toolkit.recycler.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

open class BaseRecyclerAdapter<MODEL>(private val viewCreator: (context: Context, viewType: Int) -> ViewBinder<*>?)
    : RecyclerView.Adapter<BaseViewHolder>() {

    protected val items = mutableListOf<MODEL>()
    protected var onItemClick: ((MODEL) -> Unit)? = null

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val viewBinder = viewCreator.invoke(parent.context, viewType)
                ?: throw IllegalStateException("Unable create view with type: $viewType")
        val itemView = viewBinder as? View
                ?: throw IllegalStateException("The ViewBinder instance also must be a View")
        return BaseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) =
            bindHolder(holder, items[position], onItemClick)

    @Suppress("UNCHECKED_CAST")
    open fun <T> bindHolder(holder: BaseViewHolder, model: T, onItemClick: ((T) -> Unit)? = null) {
        val binder = (holder.itemView as? ViewBinder<T>)
                ?: throw IllegalStateException("${holder.itemView::class} cannot be cast to ViewBinder<>")
        binder.bind(model)
        onItemClick?.let { listener ->
            (binder as View).setOnClickListener { listener.invoke(model) }
        }
    }

    open fun setList(newList: List<MODEL>) {
        val listSize = items.size
        items.clear()
        notifyItemRangeRemoved(0, listSize)
        addAll(newList)
    }

    open fun addAll(newItems: List<MODEL>) {
        val start = itemCount
        this.items.addAll(newItems)
        notifyItemRangeInserted(start, newItems.size)
    }

    fun withListener(onItemClick: (MODEL) -> Unit): BaseRecyclerAdapter<MODEL> {
        this.onItemClick = onItemClick
        return this
    }

}