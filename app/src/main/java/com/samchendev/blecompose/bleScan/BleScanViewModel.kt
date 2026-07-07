package com.samchendev.blecompose.bleScan

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.utlikotlin.toStateFlow
import com.samchendev.blecompose.ble.BleManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

class BleScanViewModel(
    private val bleManager: BleManager
) : ViewModel() {
    companion object {
        private const val TAG = "BleScanViewModel"
    }

    private val uiScope = viewModelScope
    private val _uiState = MutableStateFlow(createUiState())

    val uiState = combine(bleManager.scannedDevices, _uiState) { scannedDevices, uiState ->
        uiState.copy(scannedDevices = scannedDevices)
    }.toStateFlow(uiScope, createUiState())

    private fun createUiState(): BleScanUiState = BleScanUiState(
        scannedDevices = emptyList(),
        isScanning = false,
        onStartScanTrigger = ::startScan,
        onStopScanTrigger = ::stopScan
    )

    private fun startScan() {
        Log.d(TAG, "Start scan")
        updateIsScanning(true)
        bleManager.startScan()
    }

    private fun stopScan() {
        Log.d(TAG, "Stop scan")
        bleManager.stopScan()
        updateIsScanning(false)
    }

    private fun updateIsScanning(isScanning: Boolean) = _uiState.update { it.copy(isScanning = isScanning) }
}