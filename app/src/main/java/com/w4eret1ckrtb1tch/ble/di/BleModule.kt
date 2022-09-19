package com.w4eret1ckrtb1tch.ble.di

import com.polidea.rxandroidble2.RxBleClient
import com.w4eret1ckrtb1tch.ble.data.source.ble.BluetoothSource
import com.w4eret1ckrtb1tch.ble.data.system.ble.BluetoothAdvertiserService
import com.w4eret1ckrtb1tch.ble.data.system.ble.BluetoothScannerService
import com.w4eret1ckrtb1tch.ble.domain.usecase.StartAdvertisingUseCase
import com.w4eret1ckrtb1tch.ble.domain.usecase.StartScanningUseCase
import com.w4eret1ckrtb1tch.ble.domain.usecase.StopAdvertisingUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object BleModule {

    fun create() = module {

        single {
            RxBleClient.create(androidContext())
        }

        single {
            BluetoothAdvertiserService(androidContext())
        }

        single {
            BluetoothScannerService(rxBleClient = get())
        }

        single {
            BluetoothSource(
                bluetoothAdvertiserService = get(),
                bluetoothScannerService = get()
            )
        }

        factory {
            StartAdvertisingUseCase(bluetoothSource = get())
        }
        factory {
            StopAdvertisingUseCase(bluetoothSource = get())
        }
        factory {
            StartScanningUseCase(bluetoothSource = get())
        }
    }
}