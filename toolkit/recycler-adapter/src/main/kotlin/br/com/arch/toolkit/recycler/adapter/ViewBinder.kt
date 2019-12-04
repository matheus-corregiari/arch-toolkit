package br.com.arch.toolkit.recycler.adapter

/**
 * Implement this on your CustomView classes to start using your BaseRecyclerAdapter implementations like a charm =)
 */
interface ViewBinder<in MODEL> {
    fun bind(model: MODEL)
}

/**
 * Sticky implementation of ViewBinder
 * Use it in pair with StickyHeaders
 */
interface StickyViewBinder<in MODEL> : ViewBinder<MODEL>
