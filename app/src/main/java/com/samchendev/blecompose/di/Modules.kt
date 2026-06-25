package com.samchendev.blecompose.di

import com.samchendev.blecompose.ble.BleController
import com.samchendev.blecompose.ble.BleControllerImpl
import com.samchendev.blecompose.ble.BleManager
import com.samchendev.blecompose.bleConnect.BleConnectViewModel
import com.samchendev.blecompose.bleScan.BleScanViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    /*Managers*/
    single<BleController> { BleControllerImpl(androidContext()) }
    single { BleManager(get()) }

    /*Services*/

    /*Database*/

    /*Repos*/

    /*ViewModels*/
    viewModel { BleScanViewModel(get()) }
    viewModel { params -> BleConnectViewModel(params[0], params[1], get()) }
}
