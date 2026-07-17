package com.samchendev.blecompose.ble

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class BleManager(private val bleController: BleController) {
    private val _scannedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    private val _discoveredServices = MutableStateFlow<List<GattService>>(emptyList())
    private val _characteristicValues = MutableStateFlow<Map<UUID, ByteArray>>(emptyMap())

    val scannedDevices = _scannedDevices.asStateFlow()
    val connectionState = _connectionState.asStateFlow()
    val discoveredServices = _discoveredServices.asStateFlow()
    val characteristicValues = _characteristicValues.asStateFlow()

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
            onServicesDiscovered = { services -> updateDiscoveredServices(services) },
            onCharacteristicRead = { uuid, value -> updateCharacteristicValue(uuid, value) }
        )
    }

    fun disconnect() = bleController.disconnect()

    fun closeGatt() {
        bleController.closeGatt()

        updateConnectionState(ConnectionState.DISCONNECTED)
        updateDiscoveredServices(emptyList())
        _characteristicValues.update { emptyMap() }
    }

    private fun updateConnectionState(newState: ConnectionState) = _connectionState.update { newState }

    /*Discover Services*/
    private fun updateDiscoveredServices(services: List<GattService>) = _discoveredServices.update { services }

    /*Read Characteristic*/
    fun readCharacteristic(serviceUuid: UUID, characteristicUuid: UUID) =
        bleController.readCharacteristic(serviceUuid, characteristicUuid)

    private fun updateCharacteristicValue(uuid: UUID, value: ByteArray) =
        _characteristicValues.update { it + (uuid to value) }
}