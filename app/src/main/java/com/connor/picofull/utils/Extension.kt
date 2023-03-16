package com.connor.picofull.utils

import android.util.Log
import com.connor.picofull.BuildConfig
import java.util.*

fun Any.logCat(tab: String = "PICO_FULL_LOG") {
    if (!BuildConfig.DEBUG) return
    if (this is String) Log.d(tab, this) else Log.d(tab, this.toString())
}

fun Int.getHexString(): String {
    val hex = Integer.toHexString(this)
    return hex.padStart(4, '0').uppercase(Locale.getDefault())
}