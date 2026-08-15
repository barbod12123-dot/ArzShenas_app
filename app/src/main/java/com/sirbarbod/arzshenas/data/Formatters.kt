package com.sirbarbod.arzshenas.data

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

private val thousandsFormat = DecimalFormat("#,###", DecimalFormatSymbols(Locale.US))
private val priceDecimalFormat = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.US))
private val priceIntFormat = DecimalFormat("#,##0", DecimalFormatSymbols(Locale.US))

/** جداکننده‌ی هزارگان برای اعداد صحیح */
fun fmtInt(n: Double?): String {
    if (n == null) return "\u2014"
    return try {
        thousandsFormat.format(Math.round(n))
    } catch (_: Exception) {
        "\u2014"
    }
}

/** قیمت با دو رقم اعشار برای مقادیر کوچک، بدون اعشار برای مقادیر بزرگ */
fun fmtPrice(n: Double?): String {
    if (n == null) return "\u2014"
    return try {
        if (n >= 1000) priceIntFormat.format(n) else priceDecimalFormat.format(n)
    } catch (_: Exception) {
        "\u2014"
    }
}
