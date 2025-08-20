package br.com.arch.toolkit.sample.github.shared.designSystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.com.arch.toolkit.sample.github.shared.structure.core.model.DeviceType
import kotlinx.browser.window

@Composable
internal actual fun screenWidth(): Dp = window.screen.width.dp

@Composable
internal actual fun screenHeight(): Dp = window.screen.height.dp

@Composable
internal actual fun deviceType(): DeviceType = DeviceType.WEB
