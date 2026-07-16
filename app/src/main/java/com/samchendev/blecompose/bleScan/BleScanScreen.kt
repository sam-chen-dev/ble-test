package com.samchendev.blecompose.bleScan

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.utlikotlin.Button
import com.example.utlikotlin.extensions.permissions.isBluetoothPermissionGranted
import com.example.utlikotlin.extensions.permissions.isGpsEnabled
import com.example.utlikotlin.extensions.permissions.isLocationPermissionGranted
import com.example.utlikotlin.extensions.permissions.launchBluetoothPermission
import com.example.utlikotlin.extensions.permissions.launchEnableBluetooth
import com.example.utlikotlin.extensions.permissions.launchEnableGps
import com.example.utlikotlin.extensions.permissions.launchLocationPermission
import com.example.utlikotlin.showToast
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun BleScanScreen(onDeviceClick: (BluetoothDevice) -> Unit = {}) {
    val viewModel: BleScanViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val enableGpsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            uiState.onStartScanTrigger()
        } else {
            showToast(context, "GPS is required to be enabled to scan")
        }
    }

    val enableBluetoothLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (!context.isGpsEnabled()) {
                enableGpsLauncher.launchEnableGps(context)
            } else {
                uiState.onStartScanTrigger()
            }
        } else {
            showToast(context, "Bluetooth is required to be enabled to scan")
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (context.isLocationPermissionGranted()) {
            enableBluetoothLauncher.launchEnableBluetooth()
        } else {
            showToast(context, "Permission denied")
        }
    }

    val bluetoothPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (context.isBluetoothPermissionGranted()) {
            locationPermissionLauncher.launchLocationPermission()
        } else {
            showToast(context, "Permission denied")
        }
    }

    val onScanClick: () -> Unit = { bluetoothPermissionLauncher.launchBluetoothPermission() }

    BleScanContent(uiState, onScanClick, onDeviceClick)
}

@Composable
fun BleScanContent(
    uiState: BleScanUiState,
    onScanClick: () -> Unit,
    onDeviceClick: (BluetoothDevice) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Toolbar()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            BleDeviceList(uiState.scannedDevices, onDeviceClick)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                ScanButton(onScanClick, uiState.isScanning)

                Spacer(Modifier.width(16.dp))

                Button(
                    text = "Stop",
                    onClick = uiState.onStopScanTrigger,
                    isEnabled = uiState.isScanning,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Toolbar() {
    TopAppBar(
        title = { Text("Ble Scan") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
private fun RowScope.ScanButton(onClick: () -> Unit, isScanning: Boolean) {
    Button(
        onClick = onClick,
        enabled = !isScanning,
        modifier = Modifier.weight(1f)
    ) {
        if (isScanning) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Scanning...")
            }
        } else {
            Text("Scan")
        }
    }
}

@Composable
private fun ColumnScope.BleDeviceList(bleDevices: List<BluetoothDevice>, onDeviceClick: (BluetoothDevice) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1F)
    ) {
        items(bleDevices) { bleDevice ->
            BleDeviceListItem(bleDevice, onDeviceClick)
            HorizontalDivider()
        }
    }
}

@Composable
private fun BleDeviceListItem(bleDevice: BluetoothDevice, onDeviceClick: (BluetoothDevice) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDeviceClick(bleDevice) }
            .padding(16.dp),
    ) {
        Text(bleDevice.name ?: "(no name)", fontSize = 15.sp)
        Spacer(Modifier.weight(1F))
        Text(bleDevice.address, fontSize = 15.sp)
    }
}

@Preview(showBackground = true)
@Composable
private fun BleScanContentPreview() {
    BleScanContent(
        uiState = BleScanUiState(
            scannedDevices = emptyList(),
            isScanning = false,
            onStartScanTrigger = { },
            onStopScanTrigger = { }
        ),
        onScanClick = {},
        onDeviceClick = {}
    )
}