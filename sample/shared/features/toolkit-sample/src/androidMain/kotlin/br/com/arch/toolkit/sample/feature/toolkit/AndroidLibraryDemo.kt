package br.com.arch.toolkit.sample.feature.toolkit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import br.com.arch.toolkit.android.util.ContextProvider
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.component.AppButton
import br.com.arch.toolkit.sample.design.component.AppSection
import br.com.arch.toolkit.sample.design.text

@Composable
internal actual fun AndroidLibraryDemo() {
    var contextName by remember { mutableStateOf<String?>(null) }
    AppSection("ContextProvider") {
        AppButton(text(AppText.READ_CONTEXT), {
            contextName = ContextProvider.current?.javaClass?.simpleName
        })
        CodeBlock(text(AppText.RESULT), contextName ?: text(AppText.NO_CONTEXT))
        CodeBlock(text(AppText.SNIPPET), "ContextProvider.current?.javaClass?.simpleName")
    }
}
