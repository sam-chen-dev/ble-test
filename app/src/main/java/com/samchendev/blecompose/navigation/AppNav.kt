package com.samchendev.blecompose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.samchendev.blecompose.bleConnect.BleConnectScreen
import com.samchendev.blecompose.bleScan.BleScanScreen
import kotlinx.serialization.Serializable

@Serializable
data object BleScan : NavKey

@Serializable
data class BleConnect(val deviceAddress: String, val deviceName: String?) : NavKey

@Composable
fun EntryProviderScope<NavKey>.BleScanEntry(backStack: NavBackStack<NavKey>) {
    entry<BleScan> {
        BleScanScreen(
            onDeviceClick = { device ->
                backStack.add(BleConnect(device.address, device.name))
            }
        )
    }
}

@Composable
fun EntryProviderScope<NavKey>.BleConnectEntry(backStack: NavBackStack<NavKey>) {
    entry<BleConnect> { key ->
        BleConnectScreen(
            deviceAddress = key.deviceAddress,
            deviceName = key.deviceName,
            onBack = { backStack.removeLastOrNull() }
        )
    }
}
