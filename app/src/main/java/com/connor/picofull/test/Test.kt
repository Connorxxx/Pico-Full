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
    "5AA5078210160A0B0C0D".also {
        it.substring(it.length - 8).toLong(16).also {
            println(it)
        }
    }
    val o = "42".toInt(16).toChar().toString()
    val s = "31".toInt(16).toChar().toString()
    println("$o $s")
}

fun test() {
    val it = "5AA505821042000A"
    val value = it.substring(it.length - 2).toInt(16)
    println(value)
}