package com.connor.picofull.models

data class HomeData(
    var waveId: Boolean = false,
    var energy: Int = 0,
    var rate: Int = 0,
    var pulse: Long = 0,
    var spot: Int = 0,
    var switch: Boolean = false,
    var readLight: Int = 0,
    var energyBtn: Boolean = false,
    var rateBtn: Boolean = false
)
