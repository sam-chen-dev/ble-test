package com.samchendev.blecompose.ble

import android.bluetooth.BluetoothDevice

interface BleController {
    fun startScan(onDeviceFound: (BluetoothDevice) -> Unit)

    fun stopScan()

    fun connect(
        address: String,
        onConnectionStateChanged: (ConnectionState) -> Unit,
        onServicesDiscovered: (List<GattService>) -> Unit
    )

    fun disconnect()

    fun closeGatt()
}