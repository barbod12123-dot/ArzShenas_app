package com.sirbarbod.arzshenas.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.ui.unit.sp
import com.sirbarbod.arzshenas.AppUiState
import com.sirbarbod.arzshenas.AppViewModel
import com.sirbarbod.arzshenas.data.AppInfo
import com.sirbarbod.arzshenas.ui.theme.Gold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(uiState: AppUiState, viewModel: AppViewModel) {
    var showAbout by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabHeader(title = viewModel.tr("settings_title"))

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            SettingRow(icon = Icons.Filled.Language, title = viewModel.tr("language")) {
                SingleChoiceSegmentedButtonRow {
                    SegmentedButton(
                        selected = uiState.lang == "fa",
                        onClick = { viewModel.onLangChange("fa") },
                        shape = SegmentedButtonDefaults.itemShape(0, 2),
                    ) { Text("فارسی") }
                    SegmentedButton(
                        selected = uiState.lang == "en",
                        onClick = { viewModel.onLangChange("en") },
                        shape = SegmentedButtonDefaults.itemShape(1, 2),
                    ) { Text("English") }
                }
            }

            SettingRow(icon = Icons.Filled.NightsStay, title = viewModel.tr("theme")) {
                SingleChoiceSegmentedButtonRow {
                    SegmentedButton(
                        selected = uiState.themeMode == "dark",
                        onClick = { viewModel.onThemeChange("dark") },
                        shape = SegmentedButtonDefaults.itemShape(0, 2),
                    ) { Text(viewModel.tr("theme_dark")) }
                    SegmentedButton(
                        selected = uiState.themeMode == "light",
                        onClick = { viewModel.onThemeChange("light") },
                        shape = SegmentedButtonDefaults.itemShape(1, 2),
                    ) { Text(viewModel.tr("theme_light")) }
                }
            }

            SettingRow(icon = Icons.Filled.AttachMoney, title = viewModel.tr("base_currency")) {
                SingleChoiceSegmentedButtonRow {
                    SegmentedButton(
                        selected = uiState.baseCurrency == "toman",
                        onClick = { viewModel.onBaseCurrencyChange("toman") },
                        shape = SegmentedButtonDefaults.itemShape(0, 2),
                    ) { Text(viewModel.tr("toman")) }
                    SegmentedButton(
                        selected = uiState.baseCurrency == "usd",
                        onClick = { viewModel.onBaseCurrencyChange("usd") },
                        shape = SegmentedButtonDefaults.itemShape(1, 2),
                    ) { Text(viewModel.tr("dollar")) }
                }
            }

            TextButton(onClick = { showAbout = true }, modifier = Modifier.padding(top = 10.dp)) {
                Icon(Icons.Filled.Info, contentDescription = null, tint = Gold, modifier = Modifier.padding(end = 8.dp))
                Text(viewModel.tr("about"), color = Gold)
            }
        }
    }

    if (showAbout) {
        AlertDialog(
            onDismissRequest = { showAbout = false },
            title = {
                Text(
                    text = if (uiState.lang == "fa") AppInfo.NAME_FA else AppInfo.NAME_EN,
                    color = Gold,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text("${viewModel.tr("about_text")}\n\n${viewModel.tr("version")}: ${AppInfo.VERSION}")
            },
            confirmButton = {
                TextButton(onClick = { showAbout = false }) {
                    Text(viewModel.tr("close"))
                }
            },
        )
    }
}

@Composable
private fun SettingRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    control: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = Gold, modifier = Modifier.padding(end = 12.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        control()
    }
}
