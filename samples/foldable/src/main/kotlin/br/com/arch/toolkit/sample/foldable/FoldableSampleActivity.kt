package br.com.arch.toolkit.sample.foldable

import androidx.window.layout.FoldingFeature
import br.com.arch.toolkit.foldable.activity.FoldableActivity

class FoldableSampleActivity : FoldableActivity() {

    override val orientation: FoldingFeature.Orientation = FoldingFeature.Orientation.VERTICAL
    override val startViewId: Int = R.layout.activity_foldable_start_sample
    override val endViewId: Int = R.layout.activity_foldable_end_sample
}
