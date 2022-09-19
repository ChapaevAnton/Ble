package com.w4eret1ckrtb1tch.ble.domain.usecase

import com.w4eret1ckrtb1tch.ble.data.source.ble.BluetoothSource
import io.reactivex.Completable

class StartAdvertisingUseCase(
    private val bluetoothSource: BluetoothSource
) {

    operator fun invoke(): Completable = bluetoothSource.startAdvertising()
}