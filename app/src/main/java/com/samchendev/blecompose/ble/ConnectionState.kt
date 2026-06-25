package com.samchendev.blecompose.ble

enum class ConnectionState(val label: String) {
    DISCONNECTED("Disconnected"),
    CONNECTING("Connecting..."),
    CONNECTED("Connected")
}