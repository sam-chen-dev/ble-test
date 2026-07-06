package com.samchendev.blecompose.ble

import java.util.UUID

data class GattCharacteristic(
    val uuid: UUID,
    val properties: List<String>
)