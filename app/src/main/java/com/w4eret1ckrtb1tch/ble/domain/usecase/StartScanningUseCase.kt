package com.w4eret1ckrtb1tch.ble.domain.usecase

import com.w4eret1ckrtb1tch.ble.data.source.ble.BluetoothSource
import io.reactivex.Observable

class StartScanningUseCase(
    private val bluetoothSource: BluetoothSource
) {

    operator fun invoke(): Observable<String> = bluetoothSource.startScanning()
}