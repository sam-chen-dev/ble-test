package com.samchendev.blecompose.bleConnect

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.utlikotlin.Button
import com.example.utlikotlin.IconButton
import com.samchendev.blecompose.R
import com.samchendev.blecompose.ble.ConnectionState
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun BleConnectScreen(
    deviceAddress: String,
    deviceName: String?,
    onBack: () -> Unit
) {
    val viewModel: BleConnectViewModel = koinViewModel(
        parameters = { parametersOf(deviceAddress, deviceName) }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BleConnectContent(uiState, onBack)
}

@Composable
private fun BleConnectContent(
    uiState: BleConnectUiState,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Toolbar(uiState.deviceName ?: uiState.deviceAddress, onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LabeledValue("Address", uiState.deviceAddress)
            LabeledValue("Status", uiState.connectionState.label())

            Spacer(Modifier.weight(1f))

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    text = "Connect",
                    onClick = uiState.onConnectTrigger,
                    modifier = Modifier.weight(1f),
                    isEnabled = uiState.connectionState == ConnectionState.Disconnected
                )
                Spacer(Modifier.width(16.dp))
                Button(
                    text = "Disconnect",
                    onClick = uiState.onDisconnectTrigger,
                    modifier = Modifier.weight(1f),
                    isEnabled = uiState.connectionState == ConnectionState.Connected
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Toolbar(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = { IconButton(R.drawable.ic_arrow_back, "Back", onBack) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
private fun LabeledValue(label: String, value: String) {
    Column {
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 16.sp)
    }
}

private fun ConnectionState.label() = when (this) {
    ConnectionState.Disconnected -> "Disconnected"
    ConnectionState.Connecting -> "Connecting..."
    ConnectionState.Connected -> "Connected"
}

@Preview(showBackground = true)
@Composable
private fun BleConnectContentPreview() {
    BleConnectContent(
        uiState = BleConnectUiState(
            deviceName = "My BLE Device",
            deviceAddress = "DD:88:00:00:09:3D",
            connectionState = ConnectionState.Disconnected,
            onConnectTrigger = {},
            onDisconnectTrigger = {}
        ),
        onBack = {}
    )
}
