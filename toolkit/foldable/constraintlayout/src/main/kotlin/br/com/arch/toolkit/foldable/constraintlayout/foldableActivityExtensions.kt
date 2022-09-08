package br.com.arch.toolkit.foldable.constraintlayout

import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.window.layout.FoldingFeature
import br.com.arch.toolkit.foldable.OnFoldableStateChangeListener
import br.com.arch.toolkit.foldable.onFoldableStateChangeListener

fun FragmentActivity.handleFoldableStateChange(
    layout: ViewGroup,
    @IdRes reactiveGuideId: Int,
    orientation: FoldingFeature.Orientation,
    onChange: (isFolded: Boolean) -> Unit,
    onOpenFlat: ((Int, FoldingFeature.Orientation) -> Unit)? = null,
    onHalfOpen: ((Int, FoldingFeature.Orientation) -> Unit)? = null,
    onClosed: (() -> Unit)? = null
) = onFoldableStateChangeListener(
    layout,
    orientation,
    object : OnFoldableStateChangeListener {
        override fun onOpenFlat(foldPosition: Int, orientation: FoldingFeature.Orientation) {
            if (onOpenFlat != null) {
                onOpenFlat.invoke(foldPosition, orientation)
                return
            }

            ConstraintLayout.getSharedValues().fireNewValue(reactiveGuideId, foldPosition)
            onChange(false)
        }

        override fun onHalfOpen(foldPosition: Int, orientation: FoldingFeature.Orientation) {
            if (onHalfOpen != null) {
                onHalfOpen.invoke(foldPosition, orientation)
                return
            }

            ConstraintLayout.getSharedValues().fireNewValue(reactiveGuideId, foldPosition)
            onChange(false)
        }

        override fun onClosed() {
            if (onClosed != null) {
                onClosed.invoke()
                return
            }

            ConstraintLayout.getSharedValues().fireNewValue(reactiveGuideId, 0)
            onChange(true)
        }
    }
)
