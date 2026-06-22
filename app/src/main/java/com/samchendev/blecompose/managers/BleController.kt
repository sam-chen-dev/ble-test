package com.samchendev.blecompose.managers

import android.bluetooth.BluetoothDevice

interface BleController {
    fun startScan(onDeviceFound: (BluetoothDevice) -> Unit)

    fun stopScan()
}