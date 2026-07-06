package com.samchendev.blecompose.ble

import java.util.UUID

data class GattService(
    val uuid: UUID,
    val characteristics: List<GattCharacteristic>
)