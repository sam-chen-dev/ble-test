package com.samchendev.blecompose.ble

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BleManager(private val bleController: BleController) {
    private val _scannedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)

    val scannedDevices = _scannedDevices.asStateFlow()
    val connectionState = _connectionState.asStateFlow()

    /*Scan*/
    fun startScan() = bleController.startScan { device -> updateScannedDevices(device) }

    fun stopScan() = bleController.stopScan()

    private fun updateScannedDevices(newDevice: BluetoothDevice) {
        if (_scannedDevices.value.any { it.address == newDevice.address }) return

        _scannedDevices.update { it + newDevice }
    }

    /*Connect*/
    fun connect(address: String) {
        updateConnectionState(ConnectionState.CONNECTING)

        bleController.connect(address) { state -> updateConnectionState(state) }
    }

    fun disconnect() = bleController.disconnect()

    fun closeGatt() {
        bleController.closeGatt()

        updateConnectionState(ConnectionState.DISCONNECTED)
    }

    private fun updateConnectionState(newState: ConnectionState) = _connectionState.update { newState }
}