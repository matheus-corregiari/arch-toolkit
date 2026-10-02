package br.com.arch.toolkit.sample.design.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import br.com.arch.toolkit.sample.design.AppTheme

/** Secondary information stays available without dominating the task. */
@Composable
fun AppDisclosure(
    title: String,
    expandedTitle: String = title,
    initiallyExpanded: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(initiallyExpanded) }
    Column(Modifier.fillMaxWidth()) {
        TextButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.heightIn(min = AppTheme.dimen.spacingH)
        ) {
            Text(if (expanded) expandedTitle else title, color = AppTheme.color.textLink)
        }
        if (expanded) content()
    }
}
