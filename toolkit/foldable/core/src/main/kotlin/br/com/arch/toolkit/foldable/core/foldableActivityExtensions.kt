package br.com.arch.toolkit.foldable.core

import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface OnFoldableStateChangeListener {
    fun onOpenFlat(foldPosition: Int, orientation: FoldingFeature.Orientation)
    fun onHalfOpen(foldPosition: Int, orientation: FoldingFeature.Orientation)
    fun onClosed()
}

fun ComponentActivity.onNewLayoutInfo(func: (WindowLayoutInfo) -> Unit) {
    lifecycleScope.launch(Dispatchers.Main) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            WindowInfoTracker.getOrCreate(this@onNewLayoutInfo)
                .windowLayoutInfo(this@onNewLayoutInfo)
                .collect(func)
        }
    }
}

fun ComponentActivity.onFoldableStateChangeListener(
    layout: ViewGroup,
    orientation: FoldingFeature.Orientation,
    listener: OnFoldableStateChangeListener
) = onNewLayoutInfo {
    if (it.displayFeatures.isEmpty()) {
        listener.onClosed()
        return@onNewLayoutInfo
    }

    for (displayFeature in it.displayFeatures) {
        (displayFeature as? FoldingFeature)?.let { foldFeature ->
            val position = layout.getFoldPosition(foldFeature, orientation)
            if (foldFeature.state == FoldingFeature.State.HALF_OPENED) {
                listener.onHalfOpen(position, foldFeature.orientation)
            } else {
                listener.onOpenFlat(position, foldFeature.orientation)
            }
        }
    }
}
