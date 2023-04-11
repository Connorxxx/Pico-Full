package com.connor.picofull.models

data class SettingsData(
    var buzz: Boolean = false,
    var language: Int? = null,
    var volume: Int = 0,
    var fromUser: Boolean = false
)