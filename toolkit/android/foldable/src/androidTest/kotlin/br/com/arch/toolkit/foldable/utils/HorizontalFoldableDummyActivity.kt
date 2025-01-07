package br.com.arch.toolkit.foldable.utils

import androidx.window.layout.FoldingFeature
import br.com.arch.toolkit.foldable.activity.FoldableActivity

class HorizontalFoldableDummyActivity : FoldableActivity() {
    override val orientation: FoldingFeature.Orientation = FoldingFeature.Orientation.HORIZONTAL
}
