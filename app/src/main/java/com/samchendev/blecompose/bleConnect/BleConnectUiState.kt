package com.samchendev.blecompose.bleConnect

import com.samchendev.blecompose.ble.ConnectionState

data class BleConnectUiState(
    val deviceName: String?,
    val deviceAddress: String,
    val connectionState: ConnectionState,
    val onConnectTrigger: () -> Unit,
    val onDisconnectTrigger: () -> Unit
)
