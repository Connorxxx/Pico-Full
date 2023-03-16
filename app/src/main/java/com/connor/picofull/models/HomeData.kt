package com.connor.picofull.models

data class HomeData(
    var waveId: Int? = null,
    var energy: Int = 0,
    var rate: Int = 0,
    var pulse: Int = 0,
    var spot: Int = 0,
    var switch: Boolean = false,
    var readLight: Int = 0
)
