package com.sirbarbod.arzshenas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.sirbarbod.arzshenas.data.Translations
import com.sirbarbod.arzshenas.ui.screens.CryptoScreen
import com.sirbarbod.arzshenas.ui.screens.CurrencyScreen
import com.sirbarbod.arzshenas.ui.screens.GoldScreen
import com.sirbarbod.arzshenas.ui.screens.HomeScreen
import com.sirbarbod.arzshenas.ui.screens.SettingsScreen
import com.sirbarbod.arzshenas.ui.theme.ArzshenasTheme
import com.sirbarbod.arzshenas.ui.theme.Gold
import com.sirbarbod.arzshenas.ui.theme.GoldSoft
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            val isDark = uiState.themeMode == "dark"
            val layoutDirection = if (uiState.lang == "fa") LayoutDirection.Rtl else LayoutDirection.Ltr

            ArzshenasTheme(darkTheme = isDark) {
                CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides layoutDirection) {
                    val snackbarHostState = remember { SnackbarHostState() }

                    LaunchedEffect(Unit) {
                        viewModel.priceAlerts.collectLatest { msg ->
                            snackbarHostState.showSnackbar(msg)
                        }
                    }

                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                        Scaffold(
                            snackbarHost = {
                                SnackbarHost(snackbarHostState) { data ->
                                    Snackbar(
                                        containerColor = Gold,
                                        contentColor = Color.Black,
                                    ) { Text(data.visuals.message) }
                                }
                            },
                            bottomBar = {
                                AppBottomNav(
                                    navIndex = uiState.navIndex,
                                    lang = uiState.lang,
                                    onSelect = { viewModel.onNavChange(it) },
                                )
                            },
                        ) { innerPadding ->
                            Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                                if (!uiState.online) {
                                    OfflineBanner(text = Translations.t(uiState.lang, "offline_notice"))
                                }
                                when (uiState.navIndex) {
                                    0 -> HomeScreen(uiState = uiState, viewModel = viewModel)
                                    1 -> CryptoScreen(uiState = uiState, viewModel = viewModel)
                                    2 -> GoldScreen(uiState = uiState, viewModel = viewModel)
                                    3 -> CurrencyScreen(uiState = uiState, viewModel = viewModel)
                                    4 -> SettingsScreen(uiState = uiState, viewModel = viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OfflineBanner(text: String) {
    Surface(color = GoldSoft, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
            Icon(Icons.Filled.CloudOff, contentDescription = null, tint = Color.Black)
            Text(
                text = text,
                color = Color.Black,
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                modifier = Modifier.padding(start = 6.dp),
            )
        }
    }
}

@Composable
private fun AppBottomNav(navIndex: Int, lang: String, onSelect: (Int) -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        val items = listOf(
            Triple(Icons.Filled.Home, "tab_home", 0),
            Triple(Icons.Filled.CurrencyBitcoin, "tab_crypto", 1),
            Triple(Icons.Filled.Diamond, "tab_gold", 2),
            Triple(Icons.Filled.CurrencyExchange, "tab_currency", 3),
            Triple(Icons.Filled.Settings, "tab_settings", 4),
        )
        items.forEach { (icon, key, index) ->
            NavigationBarItem(
                selected = navIndex == index,
                onClick = { onSelect(index) },
                icon = { Icon(icon, contentDescription = null) },
                label = { Text(Translations.t(lang, key)) },
            )
        }
    }
}
