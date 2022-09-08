package br.com.arch.toolkit.foldable.extension

import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface OnFoldableStateChangeListener {
    fun onChangeState(isFolded: Boolean) {}
    fun onOpenFlat(foldPosition: Int, orientation: FoldingFeature.Orientation) {}
    fun onHalfOpen(foldPosition: Int, orientation: FoldingFeature.Orientation) {}
    fun onClosed() {}
    fun onWrongOrientation() {}
}

fun ComponentActivity.onNewLayoutInfo(func: (WindowLayoutInfo) -> Unit) =
    lifecycleScope.launch(Dispatchers.Main) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            WindowInfoTracker.getOrCreate(this@onNewLayoutInfo)
                .windowLayoutInfo(this@onNewLayoutInfo)
                .collect(func)
        }
    }

fun ComponentActivity.observeFoldableStateChanges(
    layout: ViewGroup,
    orientation: FoldingFeature.Orientation,
    listener: OnFoldableStateChangeListener
) = onNewLayoutInfo {
    if (it.displayFeatures.isEmpty()) {
        listener.onClosed()
        listener.onChangeState(true)
        return@onNewLayoutInfo
    }

    for (displayFeature in it.displayFeatures) {
        (displayFeature as? FoldingFeature)?.let { foldFeature ->
            val position = layout.getFoldPosition(foldFeature, orientation)
            if (orientation != foldFeature.orientation) {
                listener.onWrongOrientation()
                return@let
            }

            if (foldFeature.state == FoldingFeature.State.HALF_OPENED) {
                listener.onHalfOpen(position, foldFeature.orientation)
            } else {
                listener.onOpenFlat(position, foldFeature.orientation)
            }

            listener.onChangeState(false)
        }
    }
}

fun FragmentActivity.handleFoldableStateChange(
    layout: ViewGroup,
    @IdRes reactiveGuideId: Int,
    orientation: FoldingFeature.Orientation
) = handleFoldableStateChange(
    layout,
    reactiveGuideId,
    orientation,
    null
)

fun FragmentActivity.handleFoldableStateChange(
    layout: ViewGroup,
    @IdRes reactiveGuideId: Int,
    orientation: FoldingFeature.Orientation,
    onChangeState: ((isFolded: Boolean) -> Unit)?
) = handleFoldableStateChange(
    layout,
    reactiveGuideId,
    orientation,
    onChangeState,
    null
)

fun FragmentActivity.handleFoldableStateChange(
    layout: ViewGroup,
    @IdRes reactiveGuideId: Int,
    orientation: FoldingFeature.Orientation,
    onChangeState: ((isFolded: Boolean) -> Unit)?,
    onWrongOrientation: (() -> Unit)?
) = handleFoldableStateChange(
    layout,
    reactiveGuideId,
    orientation,
    onChangeState,
    onWrongOrientation,
    null
)

fun FragmentActivity.handleFoldableStateChange(
    layout: ViewGroup,
    @IdRes reactiveGuideId: Int,
    orientation: FoldingFeature.Orientation,
    onChangeState: ((isFolded: Boolean) -> Unit)?,
    onWrongOrientation: (() -> Unit)?,
    onOpenFlat: ((Int, FoldingFeature.Orientation) -> Unit)?
) = handleFoldableStateChange(
    layout,
    reactiveGuideId,
    orientation,
    onChangeState,
    onWrongOrientation,
    onOpenFlat,
    null
)

fun FragmentActivity.handleFoldableStateChange(
    layout: ViewGroup,
    @IdRes reactiveGuideId: Int,
    orientation: FoldingFeature.Orientation,
    onChangeState: ((isFolded: Boolean) -> Unit)?,
    onWrongOrientation: (() -> Unit)?,
    onOpenFlat: ((Int, FoldingFeature.Orientation) -> Unit)?,
    onHalfOpen: ((Int, FoldingFeature.Orientation) -> Unit)?
) = handleFoldableStateChange(
    layout,
    reactiveGuideId,
    orientation,
    onChangeState,
    onWrongOrientation,
    onOpenFlat,
    onHalfOpen,
    null
)

fun FragmentActivity.handleFoldableStateChange(
    layout: ViewGroup,
    @IdRes reactiveGuideId: Int,
    orientation: FoldingFeature.Orientation,
    onChangeState: ((isFolded: Boolean) -> Unit)?,
    onWrongOrientation: (() -> Unit)?,
    onOpenFlat: ((Int, FoldingFeature.Orientation) -> Unit)?,
    onHalfOpen: ((Int, FoldingFeature.Orientation) -> Unit)?,
    onClosed: (() -> Unit)?
) = observeFoldableStateChanges(
    layout,
    orientation,
    object : OnFoldableStateChangeListener {
        override fun onChangeState(isFolded: Boolean) {
            onChangeState?.invoke(isFolded)
        }

        override fun onOpenFlat(foldPosition: Int, orientation: FoldingFeature.Orientation) {
            ConstraintLayout.getSharedValues().fireNewValue(reactiveGuideId, foldPosition)
            onOpenFlat?.invoke(foldPosition, orientation)
        }

        override fun onHalfOpen(foldPosition: Int, orientation: FoldingFeature.Orientation) {
            ConstraintLayout.getSharedValues().fireNewValue(reactiveGuideId, foldPosition)
            onHalfOpen?.invoke(foldPosition, orientation)
        }

        override fun onClosed() {
            ConstraintLayout.getSharedValues().fireNewValue(reactiveGuideId, 0)
            onClosed?.invoke()
        }

        override fun onWrongOrientation() {
            onWrongOrientation?.invoke() ?: ConstraintLayout.getSharedValues()
                .fireNewValue(reactiveGuideId, 0)
        }
    }
)
