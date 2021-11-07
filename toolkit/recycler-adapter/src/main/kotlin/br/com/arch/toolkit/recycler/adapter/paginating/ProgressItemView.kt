package br.com.arch.toolkit.recycler.adapter.paginating

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import br.com.arch.toolkit.recycler.adapter.R
import br.com.arch.toolkit.recycler.adapter.ViewBinder

class ProgressItemView : FrameLayout, ViewBinder<Unit> {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        View.inflate(context, R.layout.progress_item_view, this)
    }

    override fun bind(model: Unit) {

    }
}