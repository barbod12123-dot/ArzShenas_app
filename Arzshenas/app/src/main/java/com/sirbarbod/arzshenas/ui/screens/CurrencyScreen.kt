package com.sirbarbod.arzshenas.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sirbarbod.arzshenas.AppUiState
import com.sirbarbod.arzshenas.AppViewModel
import com.sirbarbod.arzshenas.data.WORLD_CURRENCIES
import com.sirbarbod.arzshenas.data.WorldCurrency
import com.sirbarbod.arzshenas.data.fmtInt
import com.sirbarbod.arzshenas.data.fmtPrice
import com.sirbarbod.arzshenas.ui.theme.Gold

@Composable
fun CurrencyScreen(uiState: AppUiState, viewModel: AppViewModel) {
    val query = uiState.currencySearch.trim().lowercase()
    val list = if (query.isEmpty()) {
        WORLD_CURRENCIES
    } else {
        WORLD_CURRENCIES.filter {
            it.code.lowercase().contains(query) ||
                it.nameFa.lowercase().contains(query) ||
                it.nameEn.lowercase().contains(query)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabHeader(
            title = viewModel.tr("world_currencies"),
            onRefresh = { viewModel.onManualRefresh() },
        )
        SearchField(
            value = uiState.currencySearch,
            hint = viewModel.tr("search_hint"),
            onChange = { viewModel.onCurrencySearchChange(it) },
        )
        if (list.isEmpty()) {
            EmptyState(text = viewModel.tr("no_results"))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 90.dp),
            ) {
                items(list) { currency -> CurrencyRow(currency, uiState, viewModel) }
            }
        }
    }
}

@Composable
private fun CurrencyRow(c: WorldCurrency, uiState: AppUiState, viewModel: AppViewModel) {
    val usdIrr = uiState.iranGold.usdIrrFree
    val rateToUsd = uiState.worldRates[c.code] // چند واحد از این ارز = ۱ دلار

    val display = when {
        rateToUsd == null || usdIrr == null -> "\u2014"
        uiState.baseCurrency == "usd" -> "$${fmtPrice(1 / rateToUsd)}"
        else -> {
            val tomanPerUsd = usdIrr
            val tomanValue = tomanPerUsd / rateToUsd
            "${fmtInt(tomanValue)} ${viewModel.tr("unit_toman")}"
        }
    }

    val label = if (uiState.lang == "fa") c.nameFa else c.nameEn

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(c.flag, fontSize = 24.sp)
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Text(c.code, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
        Text(display, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Gold)
    }
}
