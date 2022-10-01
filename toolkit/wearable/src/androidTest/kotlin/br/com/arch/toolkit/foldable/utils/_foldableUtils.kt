package br.com.arch.toolkit.foldable.utils

import androidx.activity.ComponentActivity
import androidx.window.layout.FoldingFeature
import androidx.window.testing.layout.FoldingFeature
import androidx.window.testing.layout.TestWindowLayoutInfo
import androidx.window.testing.layout.WindowLayoutInfoPublisherRule

private fun getTestWindowLayoutInfo(vararg feature: FoldingFeature) =
    TestWindowLayoutInfo(feature.toList())

internal fun WindowLayoutInfoPublisherRule.openFlat(
    activity: ComponentActivity,
    orientation: FoldingFeature.Orientation = FoldingFeature.Orientation.VERTICAL
) = overrideWindowLayoutInfo(
    getTestWindowLayoutInfo(
        FoldingFeature(
            activity = activity,
            state = FoldingFeature.State.FLAT,
            orientation = orientation
        )
    )
)

internal fun WindowLayoutInfoPublisherRule.halfOpen(
    activity: ComponentActivity,
    orientation: FoldingFeature.Orientation = FoldingFeature.Orientation.VERTICAL
) = overrideWindowLayoutInfo(
    getTestWindowLayoutInfo(
        FoldingFeature(
            activity = activity,
            state = FoldingFeature.State.FLAT,
            orientation = orientation
        )
    )
)

internal fun WindowLayoutInfoPublisherRule.close() =
    overrideWindowLayoutInfo(getTestWindowLayoutInfo())
