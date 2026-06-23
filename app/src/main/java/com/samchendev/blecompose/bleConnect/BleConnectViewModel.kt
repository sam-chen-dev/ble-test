package com.samchendev.blecompose.bleConnect

import android.Manifest
import android.bluetooth.BluetoothManager
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.utlikotlin.toStateFlow
import com.samchendev.blecompose.ble.BleConnectManager
import com.samchendev.blecompose.ble.ConnectionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class BleConnectViewModel(
    private val deviceAddress: String,
    private val deviceName: String?,
    private val bluetoothManager: BluetoothManager,
    private val bleConnectManager: BleConnectManager
) : ViewModel() {
    private val uiScope = viewModelScope
    private val _uiState = MutableStateFlow(createUiState(ConnectionState.Disconnected))

    val uiState = combine(bleConnectManager.connectionState, _uiState) { connectionState, uiState ->
        uiState.copy(connectionState = connectionState)
    }.toStateFlow(uiScope, createUiState(ConnectionState.Disconnected))

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onCleared() {
        super.onCleared()
        bleConnectManager.close()
    }

    private fun createUiState(connectionState: ConnectionState) = BleConnectUiState(
        deviceName = deviceName,
        deviceAddress = deviceAddress,
        connectionState = connectionState,
        onConnectTrigger = ::connect,
        onDisconnectTrigger = ::disconnect
    )

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun connect() {
        val device = bluetoothManager.adapter.getRemoteDevice(deviceAddress)
        bleConnectManager.connect(device)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun disconnect() {
        bleConnectManager.disconnect()
    }
}
