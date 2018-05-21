package br.com.arch.toolkit.recycler.adapter

import android.content.Context
import android.support.v7.recyclerview.extensions.AsyncListDiffer
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup


abstract class BaseRecyclerAdapter<MODEL>(differ: DiffUtil.ItemCallback<MODEL> = DefaultItemDiffer()) : RecyclerView.Adapter<BaseViewHolder>() {

    @Suppress("LeakingThis")
    private val listDiffer = AsyncListDiffer<MODEL>(this, differ)
    protected val items: MutableList<MODEL>
        get() = listDiffer.currentList

    private var onItemClick: ((MODEL) -> Unit)? = null
    private val clickMap = hashMapOf<Int, (MODEL) -> Unit>()

    protected abstract fun viewCreator(context: Context, viewType: Int): ViewBinder<*>

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val viewBinder = viewCreator(parent.context, viewType)
        val itemView = viewBinder as? View
                ?: throw IllegalStateException("The ViewBinder instance also must be a View")
        return BaseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) =
            bindHolder(holder, items[position], clickMap[getItemViewType(position)] ?: onItemClick)

    @Suppress("UNCHECKED_CAST")
    open fun <T> bindHolder(holder: BaseViewHolder, model: T, onItemClick: ((T) -> Unit)? = null) {
        val binder = (holder.itemView as? ViewBinder<T>)
                ?: throw IllegalStateException("${holder.itemView::class} cannot be cast to ViewBinder<>")
        binder.bind(model)

        // Setup click listener
        onItemClick?.let { listener ->
            (binder as View).setOnClickListener { listener.invoke(model) }
        }
    }

    open fun setList(newList: List<MODEL>) {
        listDiffer.submitList(newList)
    }

    fun withListener(onItemClick: (MODEL) -> Unit): BaseRecyclerAdapter<MODEL> {
        this.onItemClick = onItemClick
        return this
    }

    open fun withListener(itemType: Int, onItemClick: (model: MODEL) -> Unit): BaseRecyclerAdapter<MODEL> {
        this.clickMap[itemType] = onItemClick
        return this
    }

    open fun removeItem(item: MODEL) = setList(items.minus(item))

    open fun addItem(item: MODEL) = setList(items.plus(item))

}