package com.connor.picofull.models

data class BackstageData(
    var loginId: Int? = null,
    var energy: Int = 0,
    var voltage: Int = 0,
    var clear: Boolean = false,
    var loginState: Boolean = false
)
