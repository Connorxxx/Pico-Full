package com.connor.picofull.test

import com.connor.picofull.utils.getHexString
import com.connor.picofull.utils.hexToString

fun main() {
    "ryxy".getHexString().also {
        println(it)
        println(it.hexToString())
    }
}