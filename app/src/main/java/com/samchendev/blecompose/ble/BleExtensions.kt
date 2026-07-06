package com.samchendev.blecompose.ble

import java.util.UUID

fun UUID.toDisplayString(): String {
    val full = toString()
    val rex = Regex("0000[0-9a-f]{4}-0000-1000-8000-00805f9b34fb", RegexOption.IGNORE_CASE)

    return if (full.matches(rex)) {
        "0x${full.substring(4, 8).uppercase()}"
    } else {
        full.uppercase()
    }
}