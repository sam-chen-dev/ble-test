package com.samchendev.blecompose.managers

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.content.Context
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class GattControllerImpl(context: Context) : GattController {
    private val applicationContext = context.applicationContext
    private val _connectionState = MutableStateFlow(ConnectionState.Disconnected)
    override val connectionState = _connectionState.asStateFlow()

    private var gatt: BluetoothGatt? = null

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            _connectionState.value = when (newState) {
                BluetoothProfile.STATE_CONNECTED -> ConnectionState.Connected
                BluetoothProfile.STATE_CONNECTING -> ConnectionState.Connecting
                else -> ConnectionState.Disconnected
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun connect(device: BluetoothDevice) {
        _connectionState.value = ConnectionState.Connecting
        gatt = device.connectGatt(applicationContext, false, gattCallback)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun disconnect() {
        gatt?.disconnect()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun close() {
        gatt?.disconnect()
        gatt?.close()
        gatt = null
        _connectionState.value = ConnectionState.Disconnected
    }
}
