package com.samchendev.blecompose.ble

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

fun UUID.toBleId(): String {
    val full = toString()
    val rex = Regex("0000[0-9a-f]{4}-0000-1000-8000-00805f9b34fb", RegexOption.IGNORE_CASE)

    return if (full.matches(rex)) {
        "0x${full.substring(4, 8).uppercase()}"
    } else {
        full.uppercase()
    }
}

fun String.toDescriptiveText() = BleFeature.entries.find { it.id == this }?.label ?: this

fun ByteArray.toText() = toString(Charsets.UTF_8)

fun ByteArray.toFloat(): Float {
    val byteOrder = ByteOrder.LITTLE_ENDIAN

    return ByteBuffer.wrap(this).order(byteOrder).float
}

fun ByteArray.toHexString() = joinToString(" ") { "%02X".format(it) }