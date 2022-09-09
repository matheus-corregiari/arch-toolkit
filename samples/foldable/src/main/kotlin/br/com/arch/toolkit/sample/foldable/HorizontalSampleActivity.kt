package br.com.arch.toolkit.sample.foldable

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.window.layout.FoldingFeature
import br.com.arch.toolkit.delegate.viewProvider
import br.com.arch.toolkit.foldable.extension.handleFoldableStateChange

class HorizontalSampleActivity : AppCompatActivity(R.layout.activity_horizontal_sample) {

    private val motionLayout: MotionLayout by viewProvider(R.id.root)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleFoldableStateChange(motionLayout, R.id.fold, FoldingFeature.Orientation.HORIZONTAL)
    }
}
