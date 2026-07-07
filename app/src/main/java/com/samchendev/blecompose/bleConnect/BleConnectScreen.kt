package com.samchendev.blecompose.bleConnect

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.utlikotlin.Button
import com.example.utlikotlin.IconButton
import com.samchendev.blecompose.R
import com.samchendev.blecompose.ble.ConnectionState
import com.samchendev.blecompose.ble.GattCharacteristic
import com.samchendev.blecompose.ble.GattService
import com.samchendev.blecompose.ble.toDisplayString
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

@Composable
fun BleConnectScreen(
    deviceAddress: String,
    deviceName: String?,
    onBack: () -> Unit
) {
    val viewModel: BleConnectViewModel = koinViewModel(parameters = { parametersOf(deviceAddress, deviceName) })
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
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LabeledValue("Address", uiState.deviceAddress)
            LabeledValue("Status", uiState.connectionState.label)

            Row(modifier = Modifier.fillMaxWidth()) {
                ConnectButton(uiState.onConnectTrigger, uiState.connectionState)

                Spacer(Modifier.width(16.dp))

                Button(
                    text = "Disconnect",
                    onClick = uiState.onDisconnectTrigger,
                    modifier = Modifier.weight(1f),
                    isEnabled = uiState.connectionState == ConnectionState.CONNECTED
                )
            }
        }

        if (uiState.services.isNotEmpty()) {
            ServicesList(uiState.services)
        }
    }
}

@Composable
private fun RowScope.ConnectButton(onClick: () -> Unit, state: ConnectionState) {
    Button(
        onClick = onClick,
        enabled = state == ConnectionState.DISCONNECTED,
        modifier = Modifier.weight(1f)
    ) {
        when (state) {
            ConnectionState.DISCONNECTED -> Text("Connect")

            ConnectionState.CONNECTING -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Connecting...")
                }
            }

            ConnectionState.CONNECTED -> Text("Connected")
        }
    }
}

@Composable
private fun ServicesList(services: List<GattService>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("Services", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }

        itemsIndexed(services) { index, service ->
            ServiceItem(service)

            if (index < services.lastIndex) {
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun ServiceItem(service: GattService) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(service.uuid.toDisplayString(), fontWeight = FontWeight.Medium, fontSize = 13.sp)

        service.characteristics.forEach {
            CharacteristicItem(it)
        }
    }
}

@Composable
private fun CharacteristicItem(characteristic: GattCharacteristic) {
    Column(
        modifier = Modifier.padding(start = 12.dp),
    ) {
        Text(characteristic.uuid.toDisplayString(), fontSize = 12.sp)
        Text(
            characteristic.properties.joinToString(" · "),
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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

@Preview(showBackground = true)
@Composable
private fun BleConnectContentPreview() {
    BleConnectContent(
        uiState = BleConnectUiState(
            deviceName = "My BLE Device",
            deviceAddress = "DD:88:00:00:09:3D",
            connectionState = ConnectionState.CONNECTED,
            services = listOf(
                GattService(
                    uuid = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb"),
                    characteristics = listOf(
                        GattCharacteristic(
                            uuid = UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb"),
                            properties = listOf("READ • WRITE")
                        )
                    )
                )
            ),
            onConnectTrigger = {},
            onDisconnectTrigger = {}
        ),
        onBack = {}
    )
}