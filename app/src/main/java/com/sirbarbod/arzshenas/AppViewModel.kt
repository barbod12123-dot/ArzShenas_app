package com.sirbarbod.arzshenas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sirbarbod.arzshenas.data.AUTO_REFRESH_MILLIS
import com.sirbarbod.arzshenas.data.CryptoCoin
import com.sirbarbod.arzshenas.data.DataService
import com.sirbarbod.arzshenas.data.IranGoldData
import com.sirbarbod.arzshenas.data.PRICE_ALERT_THRESHOLD_PCT
import com.sirbarbod.arzshenas.data.Translations
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs

data class AppUiState(
    val lang: String = "fa",
    val themeMode: String = "dark", // "dark" | "light"
    val baseCurrency: String = "toman", // "toman" | "usd"
    val online: Boolean = true,
    val loading: Boolean = true,
    val lastUpdateTs: Long? = null,
    val cryptoItems: List<CryptoCoin> = emptyList(),
    val worldRates: Map<String, Double> = emptyMap(),
    val iranGold: IranGoldData = IranGoldData.FALLBACK,
    val cryptoSearch: String = "",
    val currencySearch: String = "",
    val navIndex: Int = 0,
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val dataService = DataService(application)

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    private val _priceAlerts = MutableSharedFlow<String>(extraBufferCapacity = 4)
    val priceAlerts: SharedFlow<String> = _priceAlerts

    init {
        // بارگذاری تنظیمات ذخیره‌شده
        val lang = dataService.loadLang()
        val theme = dataService.loadTheme()
        val currency = dataService.loadBaseCurrency()
        _uiState.value = _uiState.value.copy(
            lang = lang ?: "fa",
            themeMode = theme ?: "dark",
            baseCurrency = currency ?: "toman",
        )
        refreshAll(initial = true)
        startAutoRefreshLoop()
    }

    fun tr(key: String): String = Translations.t(_uiState.value.lang, key)

    // ------------------------------------------------------------------
    // اکشن‌های UI
    // ------------------------------------------------------------------
    fun onNavChange(index: Int) {
        _uiState.value = _uiState.value.copy(navIndex = index)
    }

    fun onCryptoSearchChange(query: String) {
        _uiState.value = _uiState.value.copy(cryptoSearch = query)
    }

    fun onCurrencySearchChange(query: String) {
        _uiState.value = _uiState.value.copy(currencySearch = query)
    }

    fun onLangChange(lang: String) {
        _uiState.value = _uiState.value.copy(lang = lang)
        persistPrefs()
    }

    fun onThemeChange(theme: String) {
        _uiState.value = _uiState.value.copy(themeMode = theme)
        persistPrefs()
    }

    fun onBaseCurrencyChange(currency: String) {
        _uiState.value = _uiState.value.copy(baseCurrency = currency)
        persistPrefs()
    }

    fun onManualRefresh() {
        refreshAll(initial = false)
    }

    private fun persistPrefs() {
        val s = _uiState.value
        dataService.savePrefs(s.lang, s.themeMode, s.baseCurrency)
    }

    // ------------------------------------------------------------------
    // به‌روزرسانی داده‌ها
    // ------------------------------------------------------------------
    private fun refreshAll(initial: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)

            val prevGold = _uiState.value.iranGold.gold18
            val prevUsd = _uiState.value.iranGold.usdIrrFree

            val (crypto, ok1) = dataService.getCrypto()
            val (rates, ok2) = dataService.getWorldRates()
            val (gold, ok3) = dataService.getIranGoldCurrency()

            val online = ok1 || ok2 || ok3

            _uiState.value = _uiState.value.copy(
                cryptoItems = crypto,
                worldRates = rates,
                iranGold = gold,
                online = online,
                loading = false,
                lastUpdateTs = System.currentTimeMillis(),
            )

            if (!initial) {
                maybeNotifyPriceChange(prevGold, gold.gold18, tr("home_gold"))
                maybeNotifyPriceChange(prevUsd, gold.usdIrrFree, tr("home_dollar"))
            }
        }
    }

    private fun maybeNotifyPriceChange(oldVal: Double?, newVal: Double?, label: String) {
        try {
            if (oldVal == null || newVal == null || oldVal == 0.0) return
            val pct = abs(newVal - oldVal) / oldVal * 100
            if (pct >= PRICE_ALERT_THRESHOLD_PCT) {
                val direction = if (newVal > oldVal) "\u2191" else "\u2193"
                val msg = "${tr("notif_title")}: $label $direction ${"%.1f".format(pct)}%"
                _priceAlerts.tryEmit(msg)
            }
        } catch (_: Exception) {
        }
    }

    private fun startAutoRefreshLoop() {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(AUTO_REFRESH_MILLIS)
                try {
                    refreshAll(initial = false)
                } catch (_: Exception) {
                }
            }
        }
    }
}
