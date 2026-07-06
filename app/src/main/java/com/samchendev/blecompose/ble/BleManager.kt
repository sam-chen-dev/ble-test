package com.samchendev.blecompose.ble

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BleManager(private val bleController: BleController) {
    private val _scannedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    private val _discoveredServices = MutableStateFlow<List<GattService>>(emptyList())

    val scannedDevices = _scannedDevices.asStateFlow()
    val connectionState = _connectionState.asStateFlow()
    val discoveredServices = _discoveredServices.asStateFlow()

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

        bleController.connect(
            address = address,
            onConnectionStateChanged = { state -> updateConnectionState(state) },
            onServicesDiscovered = { services -> updateDiscoveredServices(services) }
        )
    }

    fun disconnect() = bleController.disconnect()

    fun closeGatt() {
        bleController.closeGatt()

        updateConnectionState(ConnectionState.DISCONNECTED)
        updateDiscoveredServices(emptyList())
    }

    private fun updateConnectionState(newState: ConnectionState) = _connectionState.update { newState }
    private fun updateDiscoveredServices(services: List<GattService>) = _discoveredServices.update { services }
}