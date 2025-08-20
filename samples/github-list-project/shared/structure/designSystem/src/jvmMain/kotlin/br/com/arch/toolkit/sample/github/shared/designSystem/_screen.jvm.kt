package br.com.arch.toolkit.sample.github.shared.designSystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.com.arch.toolkit.sample.github.shared.structure.core.model.DeviceType

@Composable
actual fun screenWidth(): Dp = LocalWindowInfo.current.containerSize.width.dp

@Composable
actual fun screenHeight(): Dp = LocalWindowInfo.current.containerSize.height.dp

@Composable
internal actual fun deviceType(): DeviceType = DeviceType.DESKTOP
