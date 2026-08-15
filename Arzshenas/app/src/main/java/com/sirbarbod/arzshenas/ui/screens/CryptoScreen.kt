package com.sirbarbod.arzshenas.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sirbarbod.arzshenas.AppUiState
import com.sirbarbod.arzshenas.AppViewModel
import com.sirbarbod.arzshenas.data.CryptoCoin
import com.sirbarbod.arzshenas.data.fmtPrice
import com.sirbarbod.arzshenas.ui.theme.Gold
import com.sirbarbod.arzshenas.ui.theme.GreenUp
import com.sirbarbod.arzshenas.ui.theme.RedDown

@Composable
fun CryptoScreen(uiState: AppUiState, viewModel: AppViewModel) {
    val query = uiState.cryptoSearch.trim().lowercase()
    val items = if (query.isEmpty()) {
        uiState.cryptoItems
    } else {
        uiState.cryptoItems.filter {
            it.name.lowercase().contains(query) || it.symbol.lowercase().contains(query)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabHeader(
            title = viewModel.tr("cryptocurrencies"),
            onRefresh = { viewModel.onManualRefresh() },
        )
        SearchField(
            value = uiState.cryptoSearch,
            hint = viewModel.tr("search_hint"),
            onChange = { viewModel.onCryptoSearchChange(it) },
        )
        if (items.isEmpty()) {
            EmptyState(text = viewModel.tr("no_results"))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 20.dp, end = 20.dp, bottom = 90.dp,
                ),
            ) {
                items(items) { coin -> CryptoRow(coin) }
            }
        }
    }
}

@Composable
private fun CryptoRow(c: CryptoCoin) {
    val change = c.change24h ?: 0.0
    val up = change >= 0
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = c.rank?.toString() ?: "",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.size(width = 24.dp, height = 20.dp),
        )
        if (c.image != null) {
            AsyncImage(
                model = c.image,
                contentDescription = c.name,
                modifier = Modifier
                    .size(32.dp)
                    .background(MaterialTheme.colorScheme.background, CircleShape),
            )
        } else {
            Icon(Icons.Filled.CurrencyBitcoin, contentDescription = null, tint = Gold, modifier = Modifier.size(32.dp))
        }
        Spacer(Modifier.size(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(c.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Text(c.symbol, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "$${fmtPrice(c.priceUsd)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.size(3.dp))
            Row(
                modifier = Modifier
                    .background(if (up) GreenUp else RedDown, RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text(
                    text = "${if (up) "+" else ""}${"%.2f".format(change)}%",
                    fontSize = 12.sp,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
internal fun TabHeader(title: String, onRefresh: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Gold,
        )
        if (onRefresh != null) {
            IconButton(onClick = onRefresh) {
                Icon(Icons.Filled.Refresh, contentDescription = null, tint = Gold)
            }
        }
    }
}

@Composable
internal fun SearchField(value: String, hint: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        placeholder = { Text(hint) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
    )
}

@Composable
internal fun EmptyState(text: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            Icons.Filled.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier.size(40.dp),
        )
        Spacer(Modifier.size(10.dp))
        Text(text, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
    }
}
