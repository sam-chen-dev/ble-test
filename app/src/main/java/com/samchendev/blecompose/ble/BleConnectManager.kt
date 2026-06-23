package com.samchendev.blecompose.ble

import android.Manifest
import android.bluetooth.BluetoothDevice
import androidx.annotation.RequiresPermission

class BleConnectManager(private val gattController: GattController) {
    val connectionState = gattController.connectionState

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connect(device: BluetoothDevice) = gattController.connect(device)

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun disconnect() = gattController.disconnect()

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun close() = gattController.close()
}
