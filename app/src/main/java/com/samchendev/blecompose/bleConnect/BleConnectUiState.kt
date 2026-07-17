package com.samchendev.blecompose.bleConnect

import com.samchendev.blecompose.ble.ConnectionState
import com.samchendev.blecompose.ble.GattService
import java.util.UUID

data class BleConnectUiState(
    val deviceName: String?,
    val deviceAddress: String,
    val connectionState: ConnectionState,
    val services: List<GattService>,
    val characteristicValues: Map<UUID, ByteArray>,
    val notifyingCharacteristics: Set<UUID>,
    val onConnectTrigger: () -> Unit,
    val onDisconnectTrigger: () -> Unit,
    val onCharacteristicClick: (serviceUuid: UUID, characteristicUuid: UUID) -> Unit,
    val onCharacteristicNotifyToggle: (serviceUuid: UUID, characteristicUuid: UUID, isEnable: Boolean) -> Unit
)