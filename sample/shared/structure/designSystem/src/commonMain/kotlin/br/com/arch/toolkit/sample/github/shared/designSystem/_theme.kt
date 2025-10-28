package br.com.arch.toolkit.sample.github.shared.designSystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import br.com.arch.toolkit.sample.github.shared.structure.core.model.ScreenInfo
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.CachePolicy
import coil3.request.addLastModifiedToFileCacheKey
import io.ktor.client.HttpClient
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Immutable
data object AppTheme {
    val screen: ScreenInfo
        @Composable @ReadOnlyComposable
        get() = LocalScreenInfo.current

    val color: AppColor
        @Composable @ReadOnlyComposable
        get() = LocalAppColor.current

    val dimen: AppDimen
        @Composable @ReadOnlyComposable
        get() = LocalAppDimen.current

    val textStyle: AppTextStyle
        @Composable @ReadOnlyComposable
        get() = LocalAppTextStyle.current
}

@Composable
@Suppress("FunctionNaming")
fun AppTheme(
    imageClient: HttpClient = koinInject(named("image-client")),
    content: @Composable () -> Unit
) {
    val screenInfo by getCurrentScreenInfo()
    if (screenInfo.isValid.not()) return
    setupCoil(imageClient)

    val isSystemInDarkTheme = isSystemInDarkTheme()
    val color = remember(screenInfo.theme, screenInfo.contrast, isSystemInDarkTheme) {
        AppColor(screenInfo.theme, screenInfo.contrast, isSystemInDarkTheme)
    }
    val dimen = remember(screenInfo.windowSize) { AppDimen(screenInfo.windowSize) }
    val textStyle = remember(screenInfo.windowSize) { AppTextStyle(screenInfo.windowSize) }

    println("AAAAAAAA: $screenInfo")
    MaterialTheme(
        colorScheme = color.colorScheme(),
        typography = textStyle.typography(),
        shapes = dimen.shapes(),
        content = {
            CompositionLocalProvider(
                LocalScreenInfo provides screenInfo,
                LocalAppColor provides color,
                LocalAppDimen provides dimen,
                LocalAppTextStyle provides textStyle,
                content = content
            )
        }
    )
}

@Composable
@ReadOnlyComposable
private fun setupCoil(client: HttpClient) = setSingletonImageLoaderFactory { context ->
    ImageLoader.Builder(context)
        .components { add(factory = KtorNetworkFetcherFactory(client)) }
        .memoryCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .addLastModifiedToFileCacheKey(true)
        .build()
}
