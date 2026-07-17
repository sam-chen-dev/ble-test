package com.samchendev.blecompose.ble

import java.util.UUID

data class GattCharacteristic(
    val uuid: UUID,
    val properties: List<String>
) {
    fun getFormattedValue(value: ByteArray) = when (uuid.toBleId()) {
        BleFeature.DEVICE_NAME.id -> value.toText()
        BleFeature.MANUFACTURER_NAME_STRING.id -> value.toText()
        BleFeature.MODEL_NUMBER_STRING.id -> value.toText()
        BleFeature.HARDWARE_REVISION_STRING.id -> value.toText()
        BleFeature.FIRMWARE_REVISION_STRING.id -> value.toText()
        BleFeature.CELSIUS.id -> "${value.toFloat()}°C"
        else -> value.toHexString()
    }
}