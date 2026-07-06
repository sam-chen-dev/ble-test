package com.samchendev.blecompose.bleConnect

import com.samchendev.blecompose.ble.ConnectionState
import com.samchendev.blecompose.ble.GattService

data class BleConnectUiState(
    val deviceName: String?,
    val deviceAddress: String,
    val connectionState: ConnectionState,
    val services: List<GattService>,
    val onConnectTrigger: () -> Unit,
    val onDisconnectTrigger: () -> Unit
)
