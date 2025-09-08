package br.com.arch.toolkit.sample.github.shared.designSystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import br.com.arch.toolkit.sample.github.shared.structure.core.model.WindowSize

internal val LocalAppTextStyle = compositionLocalOf<AppTextStyle> { MediumScreenTextStyle }

sealed class AppTextStyle {

    val action: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = TextStyle.Default.fontSize,
            fontFamily = TextStyle.Default.fontFamily,
        )

    val actionG: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeL,
            fontFamily = FontFamily.SansSerif,
        )

    val actionGBold: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeL,
            fontFamily = FontFamily.SansSerif,
        )

    val actionGSemiBold: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeL,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.SansSerif,
        )

    val actionM: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeM,
            fontFamily = FontFamily.SansSerif,
        )

    val actionMBold: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeM,
            fontFamily = FontFamily.SansSerif,
        )

    val actionMSemiBold: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeM,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.SansSerif,
        )

    val actionS: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeS,
            fontFamily = FontFamily.SansSerif,
        )

    val actionSBold: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeS,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
        )

    val actionSSemiBold: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeS,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.SansSerif,
        )

    val comp: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = TextStyle.Default.fontSize,
            fontFamily = TextStyle.Default.fontFamily,
        )

    val compH: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeH,
            fontFamily = FontFamily.SansSerif,
        )

    val compHMedium: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeH,
            fontFamily = FontFamily.SansSerif,
        )

    val compHRegular: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeH,
            fontFamily = FontFamily.SansSerif,
        )

    val compL: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeL,
            fontFamily = FontFamily.SansSerif,
        )

    val compLSemiBold: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeL,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.SansSerif,
        )

    val compM: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeM,
            fontFamily = FontFamily.SansSerif,
        )

    val compMSemiBold: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeM,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.SansSerif,
        )

    val compS: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeS,
            fontFamily = FontFamily.SansSerif,
        )

    val compSSemiBold: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeS,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.SansSerif,
        )

    val compXXL: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeXxl,
            fontFamily = FontFamily.SansSerif,
        )

    val compXXLMedium: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeXxl,
            fontFamily = FontFamily.SansSerif,
        )

    val compXXLRegular: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeXxl,
            fontFamily = FontFamily.SansSerif,
        )

    val compXXXL: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeXxxl,
            fontFamily = FontFamily.SansSerif,
        )

    val compXXXLMedium: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeXxxl,
            fontFamily = FontFamily.SansSerif,
        )

    val compXXXLRegular: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeXxxl,
            fontFamily = FontFamily.SansSerif,
        )

    val paragraph: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = TextStyle.Default.fontSize,
            fontFamily = TextStyle.Default.fontFamily,
        )

    val paragraphCaptionXs: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textParagraph,
            fontSize = AppTheme.dimen.fontSizeXs,
            lineHeight = AppTheme.dimen.fontLineHeightXs,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily.SansSerif,
        )

    val paragraphCaptionS: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textParagraph,
            fontSize = AppTheme.dimen.fontSizeS,
            fontFamily = FontFamily.SansSerif,
        )

    val paragraphCaptionSSemiBold: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textParagraph,
            fontSize = AppTheme.dimen.fontSizeS,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.SansSerif,
        )

    val paragraphM: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textParagraph,
            fontSize = AppTheme.dimen.fontSizeM,
            fontFamily = FontFamily.SansSerif,
            lineHeight = AppTheme.dimen.fontLineHeightM
        )

    val paragraphMBold: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textParagraph,
            fontSize = AppTheme.dimen.fontSizeM,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            lineHeight = AppTheme.dimen.fontLineHeightM
        )

    val paragraphMSemiBold: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textParagraph,
            fontSize = AppTheme.dimen.fontSizeM,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.SansSerif,
        )

    val status: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = TextStyle.Default.fontSize,
            fontFamily = TextStyle.Default.fontFamily,
        )

    val statusActive: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeS,
            fontFamily = FontFamily.SansSerif,
        )

    val statusAttention: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeS,
            fontFamily = FontFamily.SansSerif,
        )

    val statusBrand: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeS,
            fontFamily = FontFamily.SansSerif,
        )

    val statusIntermediate: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeS,
            fontFamily = FontFamily.SansSerif,
        )

    val statusNegative: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeS,
            fontFamily = FontFamily.SansSerif,
        )

    val subtitle: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = TextStyle.Default.fontSize,
            fontFamily = TextStyle.Default.fontFamily,
        )

    val subtitleXBold: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textSubtitle,
            fontSize = AppTheme.dimen.fontSizeL,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.SansSerif,
        )

    val subtitleXLight: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textSubtitle,
            fontSize = AppTheme.dimen.fontSizeL,
            fontWeight = FontWeight.Light,
            fontFamily = FontFamily.SansSerif,
        )

    val subtitleXMedium: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textSubtitle,
            fontSize = AppTheme.dimen.fontSizeL,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.SansSerif,
        )

    val subtitleXRegular: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textSubtitle,
            fontSize = AppTheme.dimen.fontSizeL,
            fontFamily = FontFamily.SansSerif,
        )

    val subtitleXSemiBold: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textSubtitle,
            fontSize = AppTheme.dimen.fontSizeL,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.SansSerif,
        )

    val subtitleXXL: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textSubtitle,
            fontSize = AppTheme.dimen.fontSizeXxl,
            fontFamily = FontFamily.SansSerif,
        )

    val title: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = TextStyle.Default.fontSize,
            fontFamily = TextStyle.Default.fontFamily,
        )

    val titleH: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeH,
            fontFamily = FontFamily.SansSerif,
        )

    val titleHMedium: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeH,
            fontFamily = FontFamily.SansSerif,
        )

    val titleHRegular: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeH,
            fontFamily = FontFamily.SansSerif,
        )

    val titleXLLight: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeXl,
            fontFamily = FontFamily.SansSerif,
        )

    val titleXLMedium: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeXl,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.SansSerif,
        )

    val titleXLRegular: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeXl,
            fontFamily = FontFamily.SansSerif,
        )

    val titleXXL: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeXxl,
            fontFamily = FontFamily.SansSerif,
        )

    val titleXXLLight: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeXxl,
            fontFamily = FontFamily.SansSerif,
        )

    val titleXXXL: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeXxxl,
            fontFamily = FontFamily.SansSerif,
        )

    val titleXXXLMedium: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeXxxl,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.SansSerif,
        )

    val titleXXXLRegular: TextStyle
        @Composable get() = TextStyle(
            color = AppTheme.color.textTitle,
            fontSize = AppTheme.dimen.fontSizeXxxl,
            fontFamily = FontFamily.SansSerif,
        )

    //TODO
    @Composable
    internal fun typography() = MaterialTheme.typography.copy(
        displayLarge = MaterialTheme.typography.displayLarge,
        displayMedium = MaterialTheme.typography.displayMedium,
        displaySmall = MaterialTheme.typography.displaySmall,
        headlineLarge = MaterialTheme.typography.headlineLarge,
        headlineMedium = MaterialTheme.typography.headlineMedium,
        headlineSmall = MaterialTheme.typography.headlineSmall,
        titleLarge = MaterialTheme.typography.titleLarge,
        titleMedium = MaterialTheme.typography.titleMedium,
        titleSmall = MaterialTheme.typography.titleSmall,
        bodyLarge = MaterialTheme.typography.bodyLarge,
        bodyMedium = MaterialTheme.typography.bodyMedium,
        bodySmall = MaterialTheme.typography.bodySmall,
        labelLarge = MaterialTheme.typography.labelLarge,
        labelMedium = MaterialTheme.typography.labelMedium,
        labelSmall = MaterialTheme.typography.labelSmall,
    )

    companion object {
        operator fun invoke(size: WindowSize) = when (size) {
            WindowSize.SMALL -> SmallScreenTextStyle
            WindowSize.MEDIUM -> MediumScreenTextStyle
            WindowSize.LARGE -> LargeScreenTextStyle
        }
    }
}

@Immutable
private data object SmallScreenTextStyle : AppTextStyle()

@Immutable
private data object MediumScreenTextStyle : AppTextStyle()

@Immutable
private data object LargeScreenTextStyle : AppTextStyle()


