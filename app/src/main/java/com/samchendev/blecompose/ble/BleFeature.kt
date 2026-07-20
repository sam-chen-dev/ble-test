package com.samchendev.blecompose.ble

enum class BleFeature(val id: String, val label: String) {
    GENERIC_ACCESS("0x1800", "Generic Access"),
    GENERIC_ATTRIBUTE("0x1801", "Generic Attribute"),
    DEVICE_INFORMATION("0x180A", "Device Information"),
    DEVICE_NAME("0x2A00", "Device Name"),
    APPEARANCE("0x2A01", "Appearance"),
    PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS("0x2A04", "Peripheral Preferred Connection Parameters"),
    CENTRAL_ADDRESS_RESOLUTION("0x2AA6", "Central Address Resolution"),
    SERVICE_CHANGED("0x2A05", "Service Changed"),
    MANUFACTURER_NAME_STRING("0x2A29", "Manufacturer Name String"),
    MODEL_NUMBER_STRING("0x2A24", "Model Number String"),
    HARDWARE_REVISION_STRING("0x2A27", "Hardware Revision String"),
    FIRMWARE_REVISION_STRING("0x2A26", "Firmware Revision String"),
    SYSTEM_ID("0x2A23", "System ID"),
    CUSTOM_SERVICE_FEA0("0xFEA0", "Custom Service (0xFEA0)"),
    CUSTOM_CHARACTERISTIC_FEA1("0xFEA1", "Custom Characteristic (0xFEA1)"),
    CUSTOM_CHARACTERISTIC_FEA2("0xFEA2", "Custom Characteristic (0xFEA2)"),
    CUSTOM_CHARACTERISTIC_FEA3("0xFEA3", "Custom Characteristic (0xFEA3)"),
    NORDIC_SECURE_DFU("0xFE59", "Nordic Secure Device Firmware Update"),
    TEMPERATURE("0x180C", "Temperature"),
    CELSIUS("0x2A6E", "Celsius"),
    INPUT_TEST("0x181C", "Input Test"),
    WRITE_TEXT("0x2A6F", "Write Text")
}