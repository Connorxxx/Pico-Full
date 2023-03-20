package com.connor.picofull.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.util.Log
import android.widget.Toast
import com.connor.picofull.App
import com.connor.picofull.BuildConfig
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

fun Any.logCat(tab: String = "PICO_FULL_LOG") {
    if (!BuildConfig.DEBUG) return
    if (this is String) Log.d(tab, this) else Log.d(tab, this.toString())
}

fun Int.getHexString(length: Int = 4): String {
    val hex = Integer.toHexString(this)
    return hex.padStart(length, '0').uppercase(Locale.getDefault())
}

fun String.getHexString(): String {
    val bytes = this.toByteArray()
    return bytes.joinToString(separator = "") { String.format("%02X", it) }
}

fun String.showToast() {
    Toast.makeText(App.app, this, Toast.LENGTH_SHORT).show()
}

fun String.hexToString(): String {
    val stringBuilder = StringBuilder()
    var i = 0
    while (i < this.length) {
        val hex = this.substring(i, i + 2)
        val intValue = hex.toInt(16)
        stringBuilder.append(intValue.toChar())
        i += 2
    }
    return stringBuilder.toString()
}

fun File.getAllFiles(): ArrayList<File> {
    val files = ArrayList<File>()
    if (isDirectory) {
        listFiles()?.forEach {
            files.addAll(it.getAllFiles())
        }
    } else {
        files.add(this)
    }
    return files
}

fun File.getVideoDuration(): Long {
    val mediaMetadataRetriever = MediaMetadataRetriever()
    mediaMetadataRetriever.setDataSource(this.absolutePath)
    return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0
}

fun Long.toSeconds(): Long = this / 1000

fun Long.formatDuration(): String {
    val hours = TimeUnit.SECONDS.toHours(this)
    val minutes = TimeUnit.SECONDS.toMinutes(this) % 60
    val seconds = this % 60
    return String.format("%d:%02d:%02d", hours, minutes, seconds)
}

fun String.cutTime() = if (this.startsWith("0:")) this.substring(this.length - 5) else this

