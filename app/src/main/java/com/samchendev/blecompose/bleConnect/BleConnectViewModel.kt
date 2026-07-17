package com.samchendev.blecompose.bleConnect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.utlikotlin.toStateFlow
import com.samchendev.blecompose.ble.BleManager
import com.samchendev.blecompose.ble.ConnectionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import java.util.UUID

class BleConnectViewModel(
    private val deviceAddress: String,
    private val deviceName: String?,
    private val bleManager: BleManager
) : ViewModel() {
    private val uiScope = viewModelScope
    private val _uiState = MutableStateFlow(createUiState())

    val uiState = combine(
        bleManager.connectionState,
        bleManager.discoveredServices,
        bleManager.characteristicValues,
        bleManager.notifyingCharacteristics,
        _uiState
    ) { connectionState, services, characteristicValues, notifyingCharacteristics, uiState ->
        uiState.copy(
            connectionState = connectionState,
            services = services,
            characteristicValues = characteristicValues,
            notifyingCharacteristics = notifyingCharacteristics
        )
    }.toStateFlow(uiScope, createUiState())

    private fun createUiState(): BleConnectUiState = BleConnectUiState(
        deviceName = deviceName,
        deviceAddress = deviceAddress,
        connectionState = ConnectionState.DISCONNECTED,
        services = emptyList(),
        characteristicValues = emptyMap(),
        notifyingCharacteristics = emptySet(),
        onConnectTrigger = ::connect,
        onDisconnectTrigger = ::disconnect,
        onCharacteristicClick = ::readCharacteristic,
        onCharacteristicNotifyToggle = ::setNotification
    )

    private fun connect() = bleManager.connect(deviceAddress)

    private fun disconnect() = bleManager.disconnect()

    private fun readCharacteristic(serviceUuid: UUID, characteristicUuid: UUID) =
        bleManager.readCharacteristic(serviceUuid, characteristicUuid)

    private fun setNotification(serviceUuid: UUID, characteristicUuid: UUID, isEnable: Boolean) =
        bleManager.setNotification(serviceUuid, characteristicUuid, isEnable)

    override fun onCleared() {
        bleManager.closeGatt()
    }
}