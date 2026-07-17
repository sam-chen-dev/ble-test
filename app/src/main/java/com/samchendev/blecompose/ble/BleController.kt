package com.samchendev.blecompose.ble

import android.bluetooth.BluetoothDevice
import java.util.UUID

interface BleController {
    fun startScan(onDeviceFound: (BluetoothDevice) -> Unit)

    fun stopScan()

    fun connect(
        address: String,
        onConnectionStateChanged: (ConnectionState) -> Unit,
        onServicesDiscovered: (List<GattService>) -> Unit,
        onCharacteristicChanged: (UUID, ByteArray) -> Unit
    )

    fun disconnect()

    fun closeGatt()

    fun readCharacteristic(serviceUuid: UUID, characteristicUuid: UUID)

    fun setNotification(serviceUuid: UUID, characteristicUuid: UUID, isEnable: Boolean)
}