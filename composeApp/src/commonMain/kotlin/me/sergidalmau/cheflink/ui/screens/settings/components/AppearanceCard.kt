package me.sergidalmau.cheflink.ui.screens.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.sergidalmau.cheflink.ui.util.ComponentSize
import me.sergidalmau.cheflink.ui.util.Language
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings

@Composable
fun AppearanceCard(
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    language: Language,
    onLanguageChange: (Language) -> Unit,
    componentSize: ComponentSize,
    onComponentSizeChange: (ComponentSize) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val strings = LocalChefLinkStrings.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        shape = MaterialTheme.shapes.large
    ) {
        Column {
            SettingItem(
                icon = Icons.Default.Language,
                title = strings.language,
                subtitle = language.displayName,
                control = {
                    var showLanguageDialog by remember { mutableStateOf(false) }
                    TextButton(onClick = { showLanguageDialog = true }) { Text(language.displayName) }
                    if (showLanguageDialog) {
                        AlertDialog(
                            onDismissRequest = { showLanguageDialog = false },
                            title = { Text(strings.language) },
                            text = {
                                Column {
                                    Language.entries.forEach { lang ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth().clickable {
                                                onLanguageChange(lang); showLanguageDialog = false
                                            }.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(selected = language == lang, onClick = null)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(lang.displayName)
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = { showLanguageDialog = false }) { Text(strings.close) }
                            }
                        )
                    }
                }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = colorScheme.outlineVariant
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(strings.componentSize, style = typography.bodyLarge, fontWeight = FontWeight.Medium)
                    Text(
                        text = when (componentSize) {
                            ComponentSize.SMALL -> strings.sizeSmall
                            ComponentSize.MEDIUM -> strings.sizeMedium
                            ComponentSize.LARGE -> strings.sizeLarge
                        },
                        style = typography.bodyMedium,
                        color = colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = componentSize.value,
                    onValueChange = { onComponentSizeChange(ComponentSize.fromFloat(it)) },
                    valueRange = 0f..2f,
                    steps = 1,
                    colors = SliderDefaults.colors(
                        thumbColor = colorScheme.primary,
                        activeTrackColor = colorScheme.primary,
                        inactiveTrackColor = colorScheme.surfaceVariant
                    )
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = colorScheme.outlineVariant
            )

            SettingItem(
                icon = Icons.Default.DarkMode,
                title = strings.darkMode,
                subtitle = strings.darkModeDesc,
                control = { Switch(checked = isDarkMode, onCheckedChange = onDarkModeChange) }
            )
        }
    }
}
