package com.sirbarbod.arzshenas.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.sirbarbod.arzshenas.data.fmtInt
import com.sirbarbod.arzshenas.data.fmtPrice
import com.sirbarbod.arzshenas.ui.theme.Gold
import com.sirbarbod.arzshenas.ui.theme.GoldBright
import com.sirbarbod.arzshenas.ui.theme.GoldSoft
import com.sirbarbod.arzshenas.ui.theme.GreenUp
import com.sirbarbod.arzshenas.ui.theme.RedDown
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(uiState: AppUiState, viewModel: AppViewModel) {
    val gold18 = uiState.iranGold.gold18
    val usdIrr = uiState.iranGold.usdIrrFree
    val btc = uiState.cryptoItems.firstOrNull { it.symbol == "BTC" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(24.dp))
        Text(
            text = viewModel.tr("app_title"),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Gold,
        )
        Text(
            text = viewModel.tr("home_summary"),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
        Spacer(Modifier.height(16.dp))

        SummaryCard(
            icon = Icons.Filled.Diamond,
            title = viewModel.tr("home_gold"),
            value = if (gold18 != null) "${fmtInt(gold18)} ${viewModel.tr("unit_toman")}" else "\u2014",
            sub = "+0.0%",
            iconBg = Gold,
        )
        Spacer(Modifier.height(12.dp))
        SummaryCard(
            icon = Icons.Filled.AttachMoney,
            title = viewModel.tr("home_dollar"),
            value = if (usdIrr != null) "${fmtInt(usdIrr)} ${viewModel.tr("unit_toman")}" else "\u2014",
            sub = "+0.0%",
            iconBg = GoldSoft,
        )
        if (btc != null) {
            Spacer(Modifier.height(12.dp))
            val change = btc.change24h ?: 0.0
            SummaryCard(
                icon = Icons.Filled.CurrencyBitcoin,
                title = viewModel.tr("home_bitcoin"),
                value = "$${fmtPrice(btc.priceUsd)}",
                sub = "${if (change >= 0) "+" else ""}${"%.2f".format(change)}%",
                iconBg = GoldBright,
            )
        }

        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { viewModel.onNavChange(1) }) {
                Icon(Icons.Filled.CurrencyBitcoin, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(6.dp))
                Text(viewModel.tr("tab_crypto"))
            }
            OutlinedButton(onClick = { viewModel.onNavChange(2) }) {
                Icon(Icons.Filled.Diamond, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(6.dp))
                Text(viewModel.tr("tab_gold"))
            }
            OutlinedButton(onClick = { viewModel.onNavChange(3) }) {
                Icon(Icons.Filled.CurrencyExchange, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(6.dp))
                Text(viewModel.tr("tab_currency"))
            }
        }

        Spacer(Modifier.height(16.dp))
        if (uiState.lastUpdateTs != null) {
            val sdf = timeFormatter()
            Text(
                text = "${viewModel.tr("last_update")}: ${sdf.format(Date(uiState.lastUpdateTs))}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

private fun timeFormatter(): SimpleDateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

@Composable
private fun SummaryCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    sub: String,
    iconBg: Color,
) {
    val up = sub.startsWith("+")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .size(44.dp)
                .background(iconBg, CircleShape),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = null, tint = Color.Black, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.size(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
        Text(sub, fontSize = 13.sp, color = if (up) GreenUp else RedDown)
    }
}
