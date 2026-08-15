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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sirbarbod.arzshenas.AppUiState
import com.sirbarbod.arzshenas.AppViewModel
import com.sirbarbod.arzshenas.data.GOLD_ITEMS
import com.sirbarbod.arzshenas.data.GoldItem
import com.sirbarbod.arzshenas.data.fmtInt
import com.sirbarbod.arzshenas.data.fmtPrice
import com.sirbarbod.arzshenas.ui.theme.Gold

@Composable
fun GoldScreen(uiState: AppUiState, viewModel: AppViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        TabHeader(
            title = viewModel.tr("gold_and_coin"),
            onRefresh = { viewModel.onManualRefresh() },
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 90.dp),
        ) {
            items(GOLD_ITEMS) { item -> GoldRow(item, uiState, viewModel) }
        }
    }
}

@Composable
private fun GoldRow(item: GoldItem, uiState: AppUiState, viewModel: AppViewModel) {
    val rawValue = uiState.iranGold.get(item.key)
    val isOunce = item.key == "gold_ounce"

    val display = when {
        rawValue == null -> "\u2014"
        isOunce -> "$${fmtPrice(rawValue)}"
        uiState.baseCurrency == "usd" -> {
            val usdIrr = uiState.iranGold.usdIrrFree ?: 1.0
            "$${fmtPrice((rawValue * 10) / usdIrr)}"
        }
        else -> "${fmtInt(rawValue)} ${viewModel.tr("unit_toman")}"
    }

    val label = if (uiState.lang == "fa") item.nameFa else item.nameEn

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .size(40.dp)
                .background(Gold, CircleShape),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Filled.Diamond, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.size(12.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = display,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Gold,
        )
    }
}
