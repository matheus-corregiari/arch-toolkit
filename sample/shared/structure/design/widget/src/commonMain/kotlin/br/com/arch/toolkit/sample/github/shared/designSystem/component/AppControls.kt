package br.com.arch.toolkit.sample.github.shared.designSystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import br.com.arch.toolkit.sample.github.shared.designSystem.AppTheme

/** A compact navigation alternative when enlarged labels cannot fit a bottom bar. */
@Composable
fun AppNavigationMenu(
    labels: List<String>,
    selected: String,
    menuLabel: String,
    onSelect: (Int) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Box(Modifier.padding(horizontal = AppTheme.dimen.spacingM)) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Menu, contentDescription = menuLabel)
            Spacer(Modifier.width(AppTheme.dimen.spacingXs))
            Text(selected, modifier = Modifier.weight(1f), color = AppTheme.color.textTitle)
            Icon(Icons.Default.ExpandMore, contentDescription = null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            labels.forEachIndexed { index, label ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        expanded = false
                        onSelect(index)
                    }
                )
            }
        }
    }
}

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = singleLine,
        modifier = modifier.fillMaxWidth(),
        shape = AppTheme.dimen.shapes().medium
    )
}

/** Choices wrap naturally for translations and enlarged fonts; selection keeps its semantics. */
@Composable
fun <T> AppChoiceGroup(
    choices: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    label: @Composable (T) -> String,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingXs),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimen.spacingXxs)
    ) {
        choices.forEach { choice ->
            FilterChip(
                selected = choice == selected,
                onClick = { onSelect(choice) },
                label = {
                    Text(
                        label(choice),
                        color = LocalContentColor.current,
                        style = AppTheme.textStyle.actionM
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = AppTheme.color.backgroundSurfaceSecondary,
                    labelColor = AppTheme.color.textParagraph,
                    selectedContainerColor = AppTheme.color.selectedSurface,
                    selectedLabelColor = AppTheme.color.selectedContent
                ),
                border = BorderStroke(
                    AppTheme.dimen.borderWidthS,
                    if (choice ==
                        selected
                    ) {
                        AppTheme.color.selectedContent
                    } else {
                        AppTheme.color.controlOutline
                    }
                )
            )
        }
    }
}
