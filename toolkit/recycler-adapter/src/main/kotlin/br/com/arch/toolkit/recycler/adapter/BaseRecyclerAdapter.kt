package br.com.arch.toolkit.recycler.adapter

import android.content.Context
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

/**
 * Basic implementation of RecyclerView.Adapter using AsyncListDiffer and CustomViews as items
 */
abstract class BaseRecyclerAdapter<MODEL>(differ: DiffUtil.ItemCallback<MODEL> = DefaultItemDiffer()) : androidx.recyclerview.widget.RecyclerView.Adapter<BaseViewHolder>() {

    @Suppress("LeakingThis")
    private val listDiffer = AsyncListDiffer<MODEL>(this, differ)

    /**
     * Current list displayed on adapter
     */
    var items: List<MODEL>
        get() = listDiffer.currentList
        private set(value) {
            listDiffer.submitList(value)
        }

    private var onItemClick: ((MODEL) -> Unit)? = null
    private val clickMap = hashMapOf<Int, (MODEL) -> Unit>()

    /**
     * @param context Android Context
     * @param viewType View Type calculated on BaseRecyclerAdapter$getItemViewType
     *
     * @return A new View instance to bind. The view must implement the ViewBinder interface
     * @throws IllegalStateException if the ViewBinder instance returned is not a View
     */
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

    /**
     * @param holder Holder holding the Custom View implementing the ViewBinder<>
     * @param onItemClick The Listener to be attached into the View, if null, the click is not configured
     * @param model Model to Bind
     *
     * @throws IllegalStateException if the Holder View doesn't implement ViewBinder
     */
    @Suppress("UNCHECKED_CAST")
    protected open fun <T> bindHolder(holder: BaseViewHolder, model: T, onItemClick: ((T) -> Unit)? = null) {
        val binder = (holder.itemView as? ViewBinder<T>)
                ?: throw IllegalStateException("${holder.itemView::class} cannot be cast to ViewBinder<>")
        binder.bind(model)

        // Setup click listener
        onItemClick?.let { listener ->
            (binder as View).setOnClickListener { listener.invoke(model) }
        }
    }

    /**
     * This method will receive a list and attach it into the adapter
     *
     * @param newList The New List to be updated into the adapter
     */
    open fun setList(newList: List<MODEL>) {
        items = newList
    }

    /**
     * Add a default Item Click
     * This listener will be called when any specific listener is configured by the viewType
     *
     * @param onItemClick The Item listener with the Model attached into the View
     */
    fun withListener(onItemClick: (MODEL) -> Unit): BaseRecyclerAdapter<MODEL> {
        this.onItemClick = onItemClick
        return this
    }

    /**
     * Add a specific Item Click
     * This listener will be called only in the click on the View by viewType
     *
     * @param onItemClick The Item listener with the Model attached into the View
     */
    fun withListener(itemType: Int, onItemClick: (model: MODEL) -> Unit): BaseRecyclerAdapter<MODEL> {
        this.clickMap[itemType] = onItemClick
        return this
    }

    /**
     * Remove the first item in the list
     *
     * @param item Item to be removed
     */
    open fun removeItem(item: MODEL) = setList(items.minus(item))

    /**
     * Add the Item at the bottom of the list
     *
     * @param item Item to be Added
     */
    open fun addItem(item: MODEL) = setList(items.plus(item))
}
