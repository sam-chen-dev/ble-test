package com.samchendev.blecompose.ble

import java.util.UUID

fun UUID.toDisplayString(): String {
    val full = toString()
    val rex = Regex("0000[0-9a-f]{4}-0000-1000-8000-00805f9b34fb", RegexOption.IGNORE_CASE)

    return if (full.matches(rex)) {
        "0x${full.substring(4, 8).uppercase()}".toDescriptiveText()
    } else {
        full.uppercase()
    }
}

fun String.toDescriptiveText() = when (this) {
    "0x1800" -> "Generic Access"
    "0x1801" -> "Generic Attribute"
    "0x180A" -> "Device Information"
    "0x2A00" -> "Device Name"
    "0x2A01" -> "Appearance"
    "0x2A04" -> "Peripheral Preferred Connection Parameters"
    "0x2AA6" -> "Central Address Resolution"
    "0x2A05" -> "Service Changed"
    "0x2A29" -> "Manufacturer Name String"
    "0x2A24" -> "Model Number String"
    "0x2A27" -> "Hardware Revision String"
    "0x2A26" -> "Firmware Revision String"
    "0x2A23" -> "System ID"
    "0xFEA0" -> "Custom Service (0xFEA0)"
    "0xFEA1" -> "Custom Characteristic (0xFEA1)"
    "0xFEA2" -> "Custom Characteristic (0xFEA2)"
    "0xFEA3" -> "Custom Characteristic (0xFEA3)"
    "0xFE59" -> "Nordic Secure Device Firmware Update"
    else -> this
}