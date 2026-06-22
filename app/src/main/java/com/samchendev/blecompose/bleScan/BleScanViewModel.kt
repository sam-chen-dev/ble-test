package com.samchendev.blecompose.bleScan

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.utlikotlin.toStateFlow
import com.samchendev.blecompose.managers.BleManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class BleScanViewModel(
    private val bleManager: BleManager
) : ViewModel() {
    private val uiScope = viewModelScope
    private val _uiState = MutableStateFlow(createUiState())

    val uiState = combine(bleManager.scannedDevices, _uiState) { scannedDevices, uiState ->
        uiState.copy(scannedDevices = scannedDevices)
    }.toStateFlow(uiScope, createUiState())

    init {

    }

    private fun createUiState(): BleScanUiState = BleScanUiState(
        scannedDevices = emptyList(),
        onStartScanTrigger = ::startScan,
        onStopScanTrigger = ::stopScan
    )

    private fun startScan() {
        Log.d("startScan()", "Start scan")
        bleManager.startScan()
    }

    private fun stopScan() {
        Log.d("stopScan()", "Stop scan")
        bleManager.stopScan()
    }
}