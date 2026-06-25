package com.samchendev.blecompose.ble

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import androidx.annotation.RequiresPermission

class BleControllerImpl(context: Context) : BleController {
    companion object {
        private const val TARGET_ADDRESS = "DD:88:00:00:09:3D"
    }

    private val applicationContext = context.applicationContext
    private val bleManager = applicationContext.getSystemService(BluetoothManager::class.java)
    private val bleScanner = bleManager.adapter.bluetoothLeScanner

    /*Scan*/
    private var scanCallback: ScanCallback? = null

    override fun startScan(onDeviceFound: (BluetoothDevice) -> Unit) {
        scanCallback = createScanCallback(onDeviceFound)

        bleScanner?.startScan(scanCallback)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun stopScan() {
        bleScanner?.stopScan(scanCallback)
    }

    private fun createScanCallback(onDeviceFound: (BluetoothDevice) -> Unit) = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            if (result.device.address != TARGET_ADDRESS) return

            onDeviceFound(result.device)
        }
    }

    /*Connect*/
    private var gatt: BluetoothGatt? = null
    private var gattCallback: BluetoothGattCallback? = null

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun connect(address: String, onConnectionStateChanged: (ConnectionState) -> Unit) {
        val device = bleManager.adapter.getRemoteDevice(address)

        gattCallback = createGattCallback(onConnectionStateChanged)
        gatt = device.connectGatt(applicationContext, false, gattCallback)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun disconnect() {
        gatt?.disconnect()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun closeGatt() {
        gatt?.disconnect()
        gatt?.close()
        gatt = null
    }

    private fun createGattCallback(
        onConnectionStateChanged: (ConnectionState) -> Unit
    ) = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTING -> onConnectionStateChanged(ConnectionState.CONNECTING)
                BluetoothProfile.STATE_CONNECTED -> onConnectionStateChanged(ConnectionState.CONNECTED)
                else -> onConnectionStateChanged(ConnectionState.DISCONNECTED)
            }
        }
    }
}