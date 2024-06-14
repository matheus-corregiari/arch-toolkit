@file:Suppress("UNCHECKED_CAST")

package br.com.arch.toolkit.sample.playground.storage

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import br.com.arch.toolkit.delegate.viewProvider
import br.com.arch.toolkit.sample.playground.R

class StorageValidationView : ConstraintLayout {

    //region Views
    private val title: AppCompatTextView by viewProvider(R.id.title)
    private val nextValue: AppCompatTextView by viewProvider(R.id.next_value)
    private val currentValue: AppCompatTextView by viewProvider(R.id.current_value)
    private val generateNextData: View by viewProvider(R.id.generate_next_data)
    private val applyNextDataIntoStorage: View by viewProvider(R.id.apply_next_data_into_storage)
    private val getFromStorage: View by viewProvider(R.id.get_from_storage)
    private val clearStorage: View by viewProvider(R.id.clear_storage)
    //endregion

    private var nextValueData: SampleData.NewData<*>? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        super(context, attrs, defStyleAttr) {
        inflate(context, R.layout.view_storage_validation, this)
        context.withStyledAttributes(attrs, R.styleable.StorageValidationView, defStyleAttr, 0) {
            title.text = getString(R.styleable.StorageValidationView_android_text)
        }
    }

    fun <T : Any> setData(data: SampleData<T>, listener: () -> SampleData.NewData<T>) {
        if (nextValueData == null) {
            nextValueData = data.asNewData()
        }
        nextValue.text = nextValueData.toString()
        currentValue.text = data.toString()
        getFromStorage.setOnClickListener { currentValue.text = data.toString() }
        applyNextDataIntoStorage.setOnClickListener {
            nextValueData?.let { data.applyNewData(it as SampleData.NewData<T>) }
            setData(data, listener)
        }
        generateNextData.setOnClickListener {
            nextValueData = listener()
            nextValue.text = nextValueData.toString()
        }
        clearStorage.setOnClickListener {
            data.delete()
            currentValue.text = data.toString()
        }
    }
}
