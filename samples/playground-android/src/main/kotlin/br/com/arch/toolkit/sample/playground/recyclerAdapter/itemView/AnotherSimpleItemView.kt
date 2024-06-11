package br.com.arch.toolkit.sample.playground.recyclerAdapter.itemView

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.appcompat.widget.AppCompatTextView
import br.com.arch.toolkit.playground.recyclerAdapter.ViewBinder
import br.com.arch.toolkit.sample.playground.R

class AnotherSimpleItemView : AppCompatTextView, ViewBinder<String> {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        R.attr.anotherSimpleItemStyle
    )

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    }

    override fun bind(model: String) {
        text = model
    }
}
