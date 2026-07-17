package com.samchendev.blecompose.ble

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import java.util.UUID

class BleControllerImpl(context: Context) : BleController {
    companion object {
        //private const val TARGET_ADDRESS = "DD:88:00:00:09:3D"
        private const val TARGET_ADDRESS = "D0:AB:58:F0:29:DB"
        private val CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }

    private val applicationContext = context.applicationContext
    private val bleManager = applicationContext.getSystemService(BluetoothManager::class.java)
    private val bleScanner = bleManager.adapter.bluetoothLeScanner

    /*Scan*/
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

    /*Connect*/
    private var gatt: BluetoothGatt? = null
    private var gattCallback: BluetoothGattCallback? = null

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun connect(
        address: String,
        onConnectionStateChanged: (ConnectionState) -> Unit,
        onServicesDiscovered: (List<GattService>) -> Unit,
        onCharacteristicChanged: (UUID, ByteArray) -> Unit
    ) {
        val device = bleManager.adapter.getRemoteDevice(address)

        gattCallback = createGattCallback(onConnectionStateChanged, onServicesDiscovered, onCharacteristicChanged)
        gatt = device.connectGatt(applicationContext, false, gattCallback)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun disconnect() {
        gatt?.disconnect()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun closeGatt() {
        gatt?.disconnect()
        gatt?.close()
        gatt = null
    }

    @SuppressLint("MissingPermission")
    private fun createGattCallback(
        onConnectionStateChanged: (ConnectionState) -> Unit,
        onServicesDiscovered: (List<GattService>) -> Unit,
        onCharacteristicChanged: (UUID, ByteArray) -> Unit
    ) = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTING -> onConnectionStateChanged(ConnectionState.CONNECTING)
                BluetoothProfile.STATE_CONNECTED -> {
                    onConnectionStateChanged(ConnectionState.CONNECTED)
                    gatt.discoverServices()
                }

                else -> onConnectionStateChanged(ConnectionState.DISCONNECTED)
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) return

            onServicesDiscovered(gatt.services.map { it.toGattService() })
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            if (status != BluetoothGatt.GATT_SUCCESS) return

            onCharacteristicChanged(characteristic.uuid, value)
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            onCharacteristicChanged(characteristic.uuid, value)
        }
    }

    /*Discover Services*/
    private fun BluetoothGattService.toGattService() = GattService(
        uuid = uuid,
        characteristics = characteristics.map { it.toGattCharacteristic() }
    )

    private fun BluetoothGattCharacteristic.toGattCharacteristic() = GattCharacteristic(
        uuid = uuid,
        properties = decodeProperties(properties)
    )

    private fun decodeProperties(properties: Int) = buildList {
        if (properties and BluetoothGattCharacteristic.PROPERTY_READ != 0) add("READ")
        if (properties and BluetoothGattCharacteristic.PROPERTY_WRITE != 0) add("WRITE")
        if (properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE != 0) add("WRITE NO RSP")
        if (properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0) add("NOTIFY")
        if (properties and BluetoothGattCharacteristic.PROPERTY_INDICATE != 0) add("INDICATE")
    }

    /*Read Characteristic*/
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun readCharacteristic(serviceUuid: UUID, characteristicUuid: UUID) {
        val characteristic = gatt?.getService(serviceUuid)?.getCharacteristic(characteristicUuid) ?: return

        gatt?.readCharacteristic(characteristic)
    }

    /*Notifications*/
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun setNotification(serviceUuid: UUID, characteristicUuid: UUID, isEnable: Boolean) {
        val characteristic = gatt?.getService(serviceUuid)?.getCharacteristic(characteristicUuid) ?: return
        val descriptor = characteristic.getDescriptor(CCCD_UUID) ?: return

        gatt?.setCharacteristicNotification(characteristic, isEnable)

        val isNotifyCapable = characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0
        val descriptorValue = when {
            !isEnable -> BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
            isNotifyCapable -> BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            else -> BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            gatt?.writeDescriptor(descriptor, descriptorValue)
        } else {
            descriptor.value = descriptorValue
            gatt?.writeDescriptor(descriptor)
        }
    }
}