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
    /**
     * Called whenever there is a state change
     *
     * @param isFolded Whether the device is folded or not
     */
    fun onChangeState(isFolded: Boolean) {}

    /**
     * Called whenever the device changes to fully open state
     *
     * @param foldPosition The position where the screen is divided (x if orientation is VERTICAL or y if orientation is HORIZONTAL)
     * @param orientation The orientation of the device
     */
    fun onOpenFlat(foldPosition: Int, orientation: FoldingFeature.Orientation) {}

    /**
     * Called whenever the device changes to half open state
     *
     * @param foldPosition The position where the screen is divided (x if orientation is VERTICAL or y if orientation is HORIZONTAL)
     * @param orientation The orientation of the device
     */
    fun onHalfOpen(foldPosition: Int, orientation: FoldingFeature.Orientation) {}

    /**
     * Called whenever the device changes to closed state
     */
    fun onClosed() {}

    /**
     * Called whenever the device changes to the unwanted orientation
     */
    fun onWrongOrientation() {}
}

/**
 * Observes the activity and notifies on device state changes
 *
 * @param func Will be called whenever there is a change
 */
fun ComponentActivity.onNewLayoutInfo(func: (WindowLayoutInfo) -> Unit) =
    lifecycleScope.launch(Dispatchers.Main) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            WindowInfoTracker.getOrCreate(this@onNewLayoutInfo)
                .windowLayoutInfo(this@onNewLayoutInfo)
                .collect(func)
        }
    }

/**
 * Observes the activity and notifies on device state changes
 *
 * @param layout The layout to be used for reference - needed whenever calculating the fold's position on screen
 * @param orientation The desired orientation - needed whenever calculating the fold's position on screen
 * @param listener Will receive a call to the correct function whenever there is a change
 */
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

/**
 * Observes the activity and handles device state changes
 *
 * @param layout The layout to be used for reference - needed whenever calculating the fold's position on screen
 * @param reactiveGuideId The ReactiveGuide's id - needed for dividing screen
 * @param orientation The desired orientation - needed whenever calculating the fold's position on screen
 */
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

/**
 * Observes the activity and handles device state changes
 *
 * @param layout The layout to be used for reference - needed whenever calculating the fold's position on screen
 * @param reactiveGuideId The ReactiveGuide's id - needed for dividing screen
 * @param orientation The desired orientation - needed whenever calculating the fold's position on screen
 * @param onChangeState Called whenever there is a state change
 */
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

/**
 * Observes the activity and handles device state changes
 *
 * @param layout The layout to be used for reference - needed whenever calculating the fold's position on screen
 * @param reactiveGuideId The ReactiveGuide's id - needed for dividing screen
 * @param orientation The desired orientation - needed whenever calculating the fold's position on screen
 * @param onChangeState Called whenever there is a state change
 * @param onWrongOrientation Called whenever the device changes to the unwanted orientation
 */
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

/**
 * Observes the activity and handles device state changes
 *
 * @param layout The layout to be used for reference - needed whenever calculating the fold's position on screen
 * @param reactiveGuideId The ReactiveGuide's id - needed for dividing screen
 * @param orientation The desired orientation - needed whenever calculating the fold's position on screen
 * @param onChangeState Called whenever there is a state change
 * @param onWrongOrientation Called whenever the device changes to the unwanted orientation
 * @param onOpenFlat Called whenever the device changes to fully open state
 */
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

/**
 * Observes the activity and handles device state changes
 *
 * @param layout The layout to be used for reference - needed whenever calculating the fold's position on screen
 * @param reactiveGuideId The ReactiveGuide's id - needed for dividing screen
 * @param orientation The desired orientation - needed whenever calculating the fold's position on screen
 * @param onChangeState Called whenever there is a state change
 * @param onWrongOrientation Called whenever the device changes to the unwanted orientation
 * @param onOpenFlat Called whenever the device changes to fully open state
 * @param onHalfOpen Called whenever the device changes to half open state
 */
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

/**
 * Observes the activity and handles device state changes
 *
 * @param layout The layout to be used for reference - needed whenever calculating the fold's position on screen
 * @param reactiveGuideId The ReactiveGuide's id - needed for dividing screen
 * @param orientation The desired orientation - needed whenever calculating the fold's position on screen
 * @param onChangeState Called whenever there is a state change
 * @param onWrongOrientation Called whenever the device changes to the unwanted orientation
 * @param onOpenFlat Called whenever the device changes to fully open state
 * @param onHalfOpen Called whenever the device changes to half open state
 * @param onClosed Called whenever the device changes to closed state
 */
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
