package com.samchendev.blecompose.managers

import android.Manifest
import android.bluetooth.BluetoothDevice
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.flow.StateFlow

enum class ConnectionState { Disconnected, Connecting, Connected }

interface GattController {
    val connectionState: StateFlow<ConnectionState>

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connect(device: BluetoothDevice)

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun disconnect()

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun close()
}
