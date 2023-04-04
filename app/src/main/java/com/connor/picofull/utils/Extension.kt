package com.connor.picofull.utils

import android.content.Context
import android.content.res.Configuration
import android.media.MediaMetadataRetriever
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.connor.picofull.App
import com.connor.picofull.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
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
    Toast.makeText(App.app, this, Toast.LENGTH_LONG).show()
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

fun String.isAlphaNumeric(): Boolean {
    val pattern = Regex("^[a-zA-Z0-9]+$")
    return pattern.matches(this)
}

suspend fun File.getAllFiles(): ArrayList<File> = withContext(Dispatchers.IO) {
    val files = ArrayList<File>()
    if (isDirectory) {
        listFiles()?.forEach { files.addAll(it.getAllFiles()) }
    } else if (name.endsWith(".mp4")) files.add(this@getAllFiles)
    files
}


fun File.getVideoDuration(): Long {
    val mediaMetadataRetriever = MediaMetadataRetriever()
    mediaMetadataRetriever.setDataSource(this.absolutePath)
    return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()?.toSeconds() ?: 0
}

fun Long.toSeconds(): Long = this / 1000

fun Long.formatDuration(): String {
    val hours = TimeUnit.SECONDS.toHours(this)
    val minutes = TimeUnit.SECONDS.toMinutes(this) % 60
    val seconds = this % 60
    return buildString {
        if (hours > 0 ) append(String.format("%d:", hours))
        append(String.format("%02d:%02d", minutes, seconds))
    }
}

fun Fragment.repeatOnStart(block: CoroutineScope.() -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            block()
        }
    }
}

fun readIniFile(filePath: String): Properties {
    val properties = Properties()
    val inputStream = FileInputStream(filePath)
    properties.load(inputStream)
    inputStream.close()
    return properties
}

fun getIniValue(filePath: String, section: String, key: String): String? {
    val properties = readIniFile(filePath)
    return properties.getProperty("$section.$key")
}

fun Context.inLand(): Boolean {
    val configuration = this.resources.configuration
    val orientation = configuration.orientation
    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        return true
    }
    return false
}

