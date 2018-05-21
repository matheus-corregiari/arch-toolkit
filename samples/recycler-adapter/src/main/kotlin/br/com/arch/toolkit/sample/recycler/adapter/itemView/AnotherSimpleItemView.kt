package br.com.arch.toolkit.sample.recycler.adapter.itemView

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import br.com.arch.toolkit.recycler.adapter.ViewBinder
import br.com.arch.toolkit.sample.recycler.adapter.R

class AnotherSimpleItemView : AppCompatTextView, ViewBinder<String> {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.anotherSimpleItemStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    }

    override fun bind(model: String) {
        text = model
    }
}