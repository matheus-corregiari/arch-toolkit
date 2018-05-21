package br.com.arch.toolkit.recycler.adapter

import android.support.v7.util.DiffUtil

class DefaultItemDiffer<MODEL> : DiffUtil.ItemCallback<MODEL>() {
    override fun areItemsTheSame(oldItem: MODEL, newItem: MODEL): Boolean {
        return newItem == oldItem
    }

    override fun areContentsTheSame(oldItem: MODEL, newItem: MODEL): Boolean {
        return newItem == oldItem
    }
}