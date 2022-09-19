package com.w4eret1ckrtb1tch.ble.data.system.ble

import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanSettings
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class BluetoothScannerService(
    private val rxBleClient: RxBleClient
) {

    private val setting = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
        .build()

    private val filter = ScanFilter.Builder()
        .setServiceUuid(BluetoothAdvertiserService.SERVICE_UUID)
        .build()

    fun startScanning(): Observable<String> {
        return rxBleClient
            .scanBleDevices(setting, filter)
            .map { scanResult -> scanResult.toString() }
    }

    fun stopScanning(): Completable {
        TODO()
    }

    fun isScanRuntimePermissionGranted(): Single<Boolean> {
        return Single.just(rxBleClient.isScanRuntimePermissionGranted)
    }

}