package com.connor.picofull.test

import com.connor.picofull.utils.getHexString
import com.connor.picofull.utils.hexToString

fun main() {
    "ryxy".getHexString().also {
        println(it)
        println(it.hexToString())
    }
    "ffffffff".toLong(16).also {
        println(it)
    }
    test()
}

fun test() {
    val it = "5AA505821042000A"
    val value = it.substring(it.length - 2).toInt(16)
    println(value)
}