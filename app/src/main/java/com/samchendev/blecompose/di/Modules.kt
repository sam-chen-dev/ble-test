package com.samchendev.blecompose.di

import android.bluetooth.BluetoothManager
import com.samchendev.blecompose.bleConnect.BleConnectViewModel
import com.samchendev.blecompose.bleScan.BleScanViewModel
import com.samchendev.blecompose.managers.BleConnectManager
import com.samchendev.blecompose.managers.BleController
import com.samchendev.blecompose.managers.BleControllerImpl
import com.samchendev.blecompose.managers.BleManager
import com.samchendev.blecompose.managers.GattController
import com.samchendev.blecompose.managers.GattControllerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    /*Managers*/
    single<BleController> { BleControllerImpl(androidContext()) }
    single { BleManager(get()) }
    single<GattController> { GattControllerImpl(androidContext()) }
    single { BleConnectManager(get()) }
    single { androidContext().getSystemService(BluetoothManager::class.java) }

    /*Services*/

    /*Database*/

    /*Repos*/

    /*ViewModels*/
    viewModel { BleScanViewModel(get()) }
    viewModel { params -> BleConnectViewModel(params.get(), params.get(), get(), get()) }
}
