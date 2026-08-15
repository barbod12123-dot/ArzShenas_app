package com.sirbarbod.arzshenas.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

// ---------------------------------------------------------------------------
// آدرس‌های سرویس‌ها (دقیقاً منطبق با نسخه‌ی اصلی پایتون)
// ---------------------------------------------------------------------------
private const val COINGECKO_URL =
    "https://api.coingecko.com/api/v3/coins/markets" +
        "?vs_currency=usd&order=market_cap_desc&per_page=50&page=1" +
        "&sparkline=false&price_change_percentage=24h"

private const val EXCHANGE_RATE_URL = "https://api.exchangerate-api.com/v4/latest/USD"

private const val GITHUB_FIAT_URL =
    "https://raw.githubusercontent.com/HosseinOdd/Navasan-API/refs/heads/main/data/fiat.json"
private const val GITHUB_GOLD_URL =
    "https://raw.githubusercontent.com/HosseinOdd/Navasan-API/refs/heads/main/data/gold.json"

// کلید رایگان BRSAPI را از https://brsapi.ir ثبت‌نام کرده و اینجا قرار دهید.
private const val BRSAPI_KEY = "BXS3v9Q4Yja7KNGY8CfQYadTcTYdahcz" // TODO: کلید خودتان را جایگزین کنید
private const val BRSAPI_GOLD_URL = "https://Api.BrsApi.ir/Market/Gold_Currency.php?key=$BRSAPI_KEY"

private const val GITHUB_DOWNLOAD_INTERVAL_MS = 300_000L // هر ۵ دقیقه یک بار دانلود

class DataService(context: Context) {

    private val appContext = context.applicationContext

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .build()

    private val prefs: SharedPreferences =
        appContext.getSharedPreferences("arzshenas_prefs", Context.MODE_PRIVATE)

    private val dataDir: File by lazy {
        File(appContext.filesDir, "data").apply { mkdirs() }
    }
    private val fiatFile: File get() = File(dataDir, "fiat.json")
    private val goldFile: File get() = File(dataDir, "gold.json")

    private var lastDownloadTime = 0L

    // ------------------------------------------------------------------
    // درخواست HTTP ساده و همزمان (روی Dispatchers.IO فراخوانی می‌شود)
    // ------------------------------------------------------------------
    private fun httpGet(url: String): String? {
        return try {
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { resp ->
                if (resp.isSuccessful) resp.body?.string() else null
            }
        } catch (e: Exception) {
            null
        }
    }

    // ------------------------------------------------------------------
    // کش‌سازی عمومی (SharedPreferences)
    // ------------------------------------------------------------------
    private fun saveCache(key: String, data: String) {
        try {
            prefs.edit().putString("cache_$key", data)
                .putLong("cache_${key}_ts", System.currentTimeMillis())
                .apply()
        } catch (_: Exception) {
        }
    }

    private fun loadCache(key: String): String? =
        try {
            prefs.getString("cache_$key", null)
        } catch (_: Exception) {
            null
        }

    // ------------------------------------------------------------------
    // کریپتو — CoinGecko
    // ------------------------------------------------------------------
    suspend fun getCrypto(): Pair<List<CryptoCoin>, Boolean> = withContext(Dispatchers.IO) {
        val body = httpGet(COINGECKO_URL)
        if (body != null) {
            try {
                val arr = JSONArray(body)
                val list = mutableListOf<CryptoCoin>()
                for (i in 0 until arr.length()) {
                    val c = arr.getJSONObject(i)
                    list.add(
                        CryptoCoin(
                            rank = c.optInt("market_cap_rank", -1).takeIf { it != -1 },
                            id = c.optString("id"),
                            symbol = c.optString("symbol").uppercase(),
                            name = c.optString("name"),
                            image = c.optString("image"),
                            priceUsd = c.optDouble("current_price", Double.NaN).takeIfFinite(),
                            change24h = c.optDouble("price_change_percentage_24h", Double.NaN).takeIfFinite(),
                            marketCap = c.optDouble("market_cap", Double.NaN).takeIfFinite(),
                        )
                    )
                }
                saveCache("crypto", body)
                return@withContext list to true
            } catch (_: Exception) {
                // ادامه به کش
            }
        }
        val cached = loadCache("crypto")
        val list = cached?.let { parseCryptoJson(it) } ?: emptyList()
        list to false
    }

    private fun parseCryptoJson(json: String): List<CryptoCoin> = try {
        val arr = JSONArray(json)
        (0 until arr.length()).map { i ->
            val c = arr.getJSONObject(i)
            CryptoCoin(
                rank = c.optInt("market_cap_rank", -1).takeIf { it != -1 },
                id = c.optString("id"),
                symbol = c.optString("symbol").uppercase(),
                name = c.optString("name"),
                image = c.optString("image"),
                priceUsd = c.optDouble("current_price", Double.NaN).takeIfFinite(),
                change24h = c.optDouble("price_change_percentage_24h", Double.NaN).takeIfFinite(),
                marketCap = c.optDouble("market_cap", Double.NaN).takeIfFinite(),
            )
        }
    } catch (_: Exception) {
        emptyList()
    }

    // ------------------------------------------------------------------
    // ارزهای جهانی — exchangerate-api
    // ------------------------------------------------------------------
    suspend fun getWorldRates(): Pair<Map<String, Double>, Boolean> = withContext(Dispatchers.IO) {
        val body = httpGet(EXCHANGE_RATE_URL)
        if (body != null) {
            try {
                val obj = JSONObject(body)
                val ratesObj = obj.getJSONObject("rates")
                val map = mutableMapOf<String, Double>()
                ratesObj.keys().forEach { k -> map[k] = ratesObj.getDouble(k) }
                saveCache("world_rates", ratesObj.toString())
                return@withContext map to true
            } catch (_: Exception) {
            }
        }
        val cached = loadCache("world_rates")
        val map = cached?.let { parseRatesJson(it) } ?: emptyMap()
        map to false
    }

    private fun parseRatesJson(json: String): Map<String, Double> = try {
        val obj = JSONObject(json)
        val map = mutableMapOf<String, Double>()
        obj.keys().forEach { k -> map[k] = obj.getDouble(k) }
        map
    } catch (_: Exception) {
        emptyMap()
    }

    // ------------------------------------------------------------------
    // طلا، سکه و دلار آزاد ایران — اولویت: GitHub > BRSAPI > کش > نمونه
    // ------------------------------------------------------------------
    suspend fun getIranGoldCurrency(): Pair<IranGoldData, Boolean> = withContext(Dispatchers.IO) {
        var out = IranGoldData.FALLBACK

        // ================= اولویت ۱: GitHub (Navasan-API) =================
        try {
            val now = System.currentTimeMillis()
            if (now - lastDownloadTime > GITHUB_DOWNLOAD_INTERVAL_MS ||
                !fiatFile.exists() || !goldFile.exists()
            ) {
                fetchFromGithub()
                lastDownloadTime = now
            }

            val navasanData = loadNavasanData()
            if (navasanData.isNotEmpty()) {
                for ((k, v) in navasanData) {
                    out = out.withValue(k, v)
                }
                saveCache("iran_gold", goldDataToJson(out))
                return@withContext out to true
            }
        } catch (_: Exception) {
            // ادامه به BRSAPI
        }

        // ================= اولویت ۲: BRSAPI =================
        if (BRSAPI_KEY.isBlank()) {
            val cached = loadCache("iran_gold")
            val data = cached?.let { jsonToGoldData(it) } ?: IranGoldData.FALLBACK
            return@withContext data to false
        }

        try {
            val body = httpGet(BRSAPI_GOLD_URL)
            if (body != null) {
                val parsed = parseBrsapi(body)
                if (parsed != null) {
                    saveCache("iran_gold", goldDataToJson(parsed))
                    return@withContext parsed to true
                }
            }
            throw Exception("empty parse result")
        } catch (_: Exception) {
            val cached = loadCache("iran_gold")
            val data = cached?.let { jsonToGoldData(it) } ?: IranGoldData.FALLBACK
            data to false
        }
    }

    private fun downloadFromGithub(url: String, savePath: File): Boolean {
        val body = httpGet(url) ?: return false
        return try {
            savePath.parentFile?.mkdirs()
            savePath.writeText(body, Charsets.UTF_8)
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun fetchFromGithub(): Boolean {
        val fiatOk = downloadFromGithub(GITHUB_FIAT_URL, fiatFile)
        val goldOk = downloadFromGithub(GITHUB_GOLD_URL, goldFile)
        return fiatOk || goldOk
    }

    /** بارگذاری داده‌های ذخیره‌شده از GitHub (ساختار Navasan-API) -> map از کلیدهای داخلی برنامه */
    private fun loadNavasanData(): Map<String, Double> {
        val result = mutableMapOf<String, Double>()

        // ارزها (fiat.json)
        try {
            if (fiatFile.exists()) {
                val data = JSONObject(fiatFile.readText(Charsets.UTF_8))
                if (data.has("usd")) {
                    val usdObj = data.getJSONObject("usd")
                    result["usd_irr_free"] = usdObj.optDouble("value", 0.0)
                }
            }
        } catch (_: Exception) {
        }

        // طلا (gold.json)
        try {
            if (goldFile.exists()) {
                val data = JSONObject(goldFile.readText(Charsets.UTF_8))
                val mapping = mapOf(
                    "18ayar" to "gold_18",
                    "sekkeh" to "coin_emami",
                    "nim" to "coin_half",
                    "rob" to "coin_quarter",
                )
                for ((navasanKey, programKey) in mapping) {
                    if (data.has(navasanKey)) {
                        val v = data.getJSONObject(navasanKey).opt("value")
                        if (v != null) {
                            result[programKey] = v.toString().toDoubleOrNull() ?: continue
                        }
                    }
                }
                if (result.containsKey("gold_18") && !result.containsKey("gold_24")) {
                    result["gold_24"] = result["gold_18"]!! * 1.333
                }
                if (data.has("xau")) {
                    val v = data.getJSONObject("xau").opt("value")
                    if (v != null) {
                        result["gold_ounce"] = v.toString().toDoubleOrNull() ?: return result
                    }
                }
            }
        } catch (_: Exception) {
        }

        return result
    }

    private fun parseBrsapi(rawText: String): IranGoldData? {
        return try {
            var out = IranGoldData.FALLBACK
            val items = mutableListOf<JSONObject>()

            val trimmed = rawText.trim()
            if (trimmed.startsWith("{")) {
                val obj = JSONObject(trimmed)
                val goldArr = obj.optJSONArray("gold") ?: JSONArray()
                val curArr = obj.optJSONArray("currency") ?: JSONArray()
                for (i in 0 until goldArr.length()) items.add(goldArr.getJSONObject(i))
                for (i in 0 until curArr.length()) items.add(curArr.getJSONObject(i))
            } else if (trimmed.startsWith("[")) {
                val arr = JSONArray(trimmed)
                for (i in 0 until arr.length()) items.add(arr.getJSONObject(i))
            }

            val keyMap = mapOf(
                "geram18" to "gold_18",
                "gold_gram18" to "gold_18",
                "geram24" to "gold_24",
                "gold_gram24" to "gold_24",
                "ons" to "gold_ounce",
                "gold_ounce" to "gold_ounce",
                "sekee" to "coin_emami",
                "emami" to "coin_emami",
                "nim" to "coin_half",
                "rob" to "coin_quarter",
                "usd" to "usd_irr_free",
                "dollar" to "usd_irr_free",
            )

            for (item in items) {
                val symbol = (item.optString("symbol").ifBlank { item.optString("name") }).lowercase()
                val priceRaw = item.opt("price") ?: item.opt("value")
                if (priceRaw != null) {
                    for ((srcKey, dstKey) in keyMap) {
                        if (symbol.contains(srcKey)) {
                            val priceStr = priceRaw.toString().replace(",", "")
                            priceStr.toDoubleOrNull()?.let { out = out.withValue(dstKey, it) }
                        }
                    }
                }
            }
            out
        } catch (_: Exception) {
            null
        }
    }

    private fun goldDataToJson(data: IranGoldData): String {
        val obj = JSONObject()
        obj.put("gold_18", data.gold18)
        obj.put("gold_24", data.gold24)
        obj.put("gold_ounce", data.goldOunce)
        obj.put("coin_emami", data.coinEmami)
        obj.put("coin_half", data.coinHalf)
        obj.put("coin_quarter", data.coinQuarter)
        obj.put("usd_irr_free", data.usdIrrFree)
        return obj.toString()
    }

    private fun jsonToGoldData(json: String): IranGoldData = try {
        val obj = JSONObject(json)
        IranGoldData(
            gold18 = obj.optDouble("gold_18", IranGoldData.FALLBACK.gold18 ?: 0.0),
            gold24 = obj.optDouble("gold_24", IranGoldData.FALLBACK.gold24 ?: 0.0),
            goldOunce = obj.optDouble("gold_ounce", IranGoldData.FALLBACK.goldOunce ?: 0.0),
            coinEmami = obj.optDouble("coin_emami", IranGoldData.FALLBACK.coinEmami ?: 0.0),
            coinHalf = obj.optDouble("coin_half", IranGoldData.FALLBACK.coinHalf ?: 0.0),
            coinQuarter = obj.optDouble("coin_quarter", IranGoldData.FALLBACK.coinQuarter ?: 0.0),
            usdIrrFree = obj.optDouble("usd_irr_free", IranGoldData.FALLBACK.usdIrrFree ?: 0.0),
        )
    } catch (_: Exception) {
        IranGoldData.FALLBACK
    }

    // ------------------------------------------------------------------
    // تنظیمات کاربر (زبان، تم، واحد پول پایه)
    // ------------------------------------------------------------------
    fun loadLang(): String? = prefs.getString("lang", null)
    fun loadTheme(): String? = prefs.getString("theme", null)
    fun loadBaseCurrency(): String? = prefs.getString("currency", null)

    fun savePrefs(lang: String, theme: String, currency: String) {
        prefs.edit()
            .putString("lang", lang)
            .putString("theme", theme)
            .putString("currency", currency)
            .apply()
    }
}

private fun Double.takeIfFinite(): Double? = if (this.isNaN() || this.isInfinite()) null else this
