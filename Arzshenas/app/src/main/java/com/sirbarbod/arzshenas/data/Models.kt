package com.sirbarbod.arzshenas.data

data class CryptoCoin(
    val rank: Int? = null,
    val id: String? = null,
    val symbol: String = "",
    val name: String = "",
    val image: String? = null,
    val priceUsd: Double? = null,
    val change24h: Double? = null,
    val marketCap: Double? = null,
)

data class IranGoldData(
    val gold18: Double? = 38_500_000.0,
    val gold24: Double? = 51_300_000.0,
    val goldOunce: Double? = 2650.0, // دلار
    val coinEmami: Double? = 385_000_000.0,
    val coinHalf: Double? = 195_000_000.0,
    val coinQuarter: Double? = 110_000_000.0,
    val usdIrrFree: Double? = 1_085_000.0, // نرخ آزاد دلار به تومان
) {
    fun get(key: String): Double? = when (key) {
        "gold_18" -> gold18
        "gold_24" -> gold24
        "gold_ounce" -> goldOunce
        "coin_emami" -> coinEmami
        "coin_half" -> coinHalf
        "coin_quarter" -> coinQuarter
        "usd_irr_free" -> usdIrrFree
        else -> null
    }

    fun withValue(key: String, value: Double): IranGoldData = when (key) {
        "gold_18" -> copy(gold18 = value)
        "gold_24" -> copy(gold24 = value)
        "gold_ounce" -> copy(goldOunce = value)
        "coin_emami" -> copy(coinEmami = value)
        "coin_half" -> copy(coinHalf = value)
        "coin_quarter" -> copy(coinQuarter = value)
        "usd_irr_free" -> copy(usdIrrFree = value)
        else -> this
    }

    companion object {
        val FALLBACK = IranGoldData()
    }
}

data class GoldItem(val key: String, val nameFa: String, val nameEn: String)

val GOLD_ITEMS = listOf(
    GoldItem("gold_18", "طلای ۱۸ عیار (هر گرم)", "18K Gold (per gram)"),
    GoldItem("gold_24", "طلای ۲۴ عیار (هر گرم)", "24K Gold (per gram)"),
    GoldItem("gold_ounce", "انس جهانی طلا", "Gold Ounce (Global)"),
    GoldItem("coin_emami", "سکه امامی", "Emami Coin"),
    GoldItem("coin_half", "نیم سکه", "Half Coin"),
    GoldItem("coin_quarter", "ربع سکه", "Quarter Coin"),
)

data class WorldCurrency(val code: String, val nameFa: String, val nameEn: String, val flag: String)

val WORLD_CURRENCIES = listOf(
    WorldCurrency("USD", "دلار آمریکا", "US Dollar", "\uD83C\uDDFA\uD83C\uDDF8"),
    WorldCurrency("EUR", "یورو", "Euro", "\uD83C\uDDEA\uD83C\uDDFA"),
    WorldCurrency("GBP", "پوند انگلیس", "British Pound", "\uD83C\uDDEC\uD83C\uDDE7"),
    WorldCurrency("AED", "درهم امارات", "UAE Dirham", "\uD83C\uDDE6\uD83C\uDDEA"),
    WorldCurrency("TRY", "لیر ترکیه", "Turkish Lira", "\uD83C\uDDF9\uD83C\uDDF7"),
    WorldCurrency("CNY", "یوان چین", "Chinese Yuan", "\uD83C\uDDE8\uD83C\uDDF3"),
    WorldCurrency("JPY", "ین ژاپن", "Japanese Yen", "\uD83C\uDDEF\uD83C\uDDF5"),
    WorldCurrency("CAD", "دلار کانادا", "Canadian Dollar", "\uD83C\uDDE8\uD83C\uDDE6"),
    WorldCurrency("AUD", "دلار استرالیا", "Australian Dollar", "\uD83C\uDDE6\uD83C\uDDFA"),
    WorldCurrency("CHF", "فرانک سوئیس", "Swiss Franc", "\uD83C\uDDE8\uD83C\uDDED"),
    WorldCurrency("SEK", "کرون سوئد", "Swedish Krona", "\uD83C\uDDF8\uD83C\uDDEA"),
    WorldCurrency("RUB", "روبل روسیه", "Russian Ruble", "\uD83C\uDDF7\uD83C\uDDFA"),
    WorldCurrency("INR", "روپیه هند", "Indian Rupee", "\uD83C\uDDEE\uD83C\uDDF3"),
    WorldCurrency("KWD", "دینار کویت", "Kuwaiti Dinar", "\uD83C\uDDF0\uD83C\uDDFC"),
    WorldCurrency("SAR", "ریال عربستان", "Saudi Riyal", "\uD83C\uDDF8\uD83C\uDDE6"),
    WorldCurrency("IQD", "دینار عراق", "Iraqi Dinar", "\uD83C\uDDEE\uD83C\uDDF6"),
    WorldCurrency("QAR", "ریال قطر", "Qatari Riyal", "\uD83C\uDDF6\uD83C\uDDE6"),
    WorldCurrency("OMR", "ریال عمان", "Omani Rial", "\uD83C\uDDF4\uD83C\uDDF2"),
)

object AppInfo {
    const val NAME_FA = "ارزش‌شناس"
    const val NAME_EN = "Arzshenas"
    const val VERSION = "1.0.0"
}

// ------------------------------------------------------------------
// تنظیمات عمومی
// ------------------------------------------------------------------
const val AUTO_REFRESH_MILLIS = 300_000L // ۵ دقیقه
const val PRICE_ALERT_THRESHOLD_PCT = 1.5 // درصد تغییر لازم برای اعلان
