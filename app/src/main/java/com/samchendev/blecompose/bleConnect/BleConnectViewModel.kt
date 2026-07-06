package com.samchendev.blecompose.bleConnect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.utlikotlin.toStateFlow
import com.samchendev.blecompose.ble.BleManager
import com.samchendev.blecompose.ble.ConnectionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

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
        _uiState
    ) { connectionState, services, uiState ->
        uiState.copy(connectionState = connectionState, services = services)
    }.toStateFlow(uiScope, createUiState())

    private fun createUiState(): BleConnectUiState = BleConnectUiState(
        deviceName = deviceName,
        deviceAddress = deviceAddress,
        connectionState = ConnectionState.DISCONNECTED,
        services = emptyList(),
        onConnectTrigger = ::connect,
        onDisconnectTrigger = ::disconnect
    )

    private fun connect() = bleManager.connect(deviceAddress)

    private fun disconnect() = bleManager.disconnect()

    override fun onCleared() {
        bleManager.closeGatt()
    }
}