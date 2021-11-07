package br.com.arch.toolkit.sample.recycler.adapter.paginating

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import br.com.arch.toolkit.recycler.adapter.ViewBinder
import br.com.arch.toolkit.sample.recycler.adapter.R

class NumberView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr), ViewBinder<String> {

    override fun bind(model: String) {
        textSize = 16f
        setTextColor(context.resources.getColor(R.color.colorPrimary))
        text = model
    }

}