package br.com.arch.toolkit.recyclerAdapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

/**
 * Default implementation of DiffUtil.ItemCallback
 */
class DefaultItemDiffer<MODEL : Any> : DiffUtil.ItemCallback<MODEL>() {
    override fun areItemsTheSame(oldItem: MODEL, newItem: MODEL): Boolean {
        return newItem == oldItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: MODEL, newItem: MODEL): Boolean {
        return newItem == oldItem
    }
}
