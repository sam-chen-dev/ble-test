package com.samchendev.blecompose.bleScan

import android.bluetooth.BluetoothDevice

data class BleScanUiState(
    val scannedDevices: List<BluetoothDevice>,
    val isScanning: Boolean,
    val onStartScanTrigger: () -> Unit,
    val onStopScanTrigger: () -> Unit,
    val onDeviceClick: (BluetoothDevice) -> Unit = {  }
)