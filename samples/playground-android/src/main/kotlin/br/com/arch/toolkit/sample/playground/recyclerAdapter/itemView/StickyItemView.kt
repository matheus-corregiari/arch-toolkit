package br.com.arch.toolkit.sample.playground.recyclerAdapter.itemView

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import br.com.arch.toolkit.recyclerAdapter.ViewBinder
import br.com.arch.toolkit.sample.playground.R
import br.com.arch.toolkit.sample.playground.recyclerAdapter.StickyHeadersActivity

sealed class StickyItemView {
    abstract class View(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : AppCompatTextView(
        context,
        attrs,
        defStyleAttr
    ),
        ViewBinder<StickyHeadersActivity.StickyHeaderModelExample> {

        init {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        override fun bind(model: StickyHeadersActivity.StickyHeaderModelExample) {
            text = model.title
        }
    }

    class Header : View {
        constructor(context: Context) : this(context, null)
        constructor(context: Context, attrs: AttributeSet?) : this(
            context,
            attrs,
            R.attr.stickyItemStyle
        )

        constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
        )
    }

    class Item : View {
        constructor(context: Context) : this(context, null)
        constructor(context: Context, attrs: AttributeSet?) : this(
            context,
            attrs,
            R.attr.simpleItemStyle
        )

        constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
        )
    }
}
