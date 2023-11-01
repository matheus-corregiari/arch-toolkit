package br.com.arch.toolkit.sample.foldable

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.com.arch.toolkit.delegate.viewProvider

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val buttonVerticalSample: View by viewProvider(R.id.bt_vertical_example)
    private val buttonHorizontalSample: View by viewProvider(R.id.bt_horizontal_example)
    private val buttonFoldableActivitySample: View by viewProvider(R.id.bt_foldable_example)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        buttonVerticalSample.setOnClickListener {
            startActivity<VerticalSampleActivity>()
        }

        buttonHorizontalSample.setOnClickListener {
            startActivity<HorizontalSampleActivity>()
        }

        buttonFoldableActivitySample.setOnClickListener {
            startActivity<FoldableSampleActivity>()
        }
    }

    private inline fun <reified T> startActivity() = startActivity(Intent(this, T::class.java))
}
