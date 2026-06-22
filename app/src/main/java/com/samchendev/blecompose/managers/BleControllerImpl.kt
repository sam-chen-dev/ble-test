package com.samchendev.blecompose.managers

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
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
}