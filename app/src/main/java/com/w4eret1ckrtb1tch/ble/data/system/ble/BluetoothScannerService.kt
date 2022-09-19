package com.w4eret1ckrtb1tch.ble.data.system.ble

import android.util.Log
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanSettings
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.Disposable

class BluetoothScannerService(
    private val rxBleClient: RxBleClient
) {

    private var scanningDisposable: Disposable? = null

    private val setting = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
        .build()

    private val filter = ScanFilter.Builder()
        .setServiceUuid(BluetoothAdvertiserService.SERVICE_UUID)
        .build()

    fun startScanning(): Completable {
        return Completable.fromAction {
            rxBleClient
                .scanBleDevices(setting, filter)
                .map { scanResult ->
                    scanResult.toString()
                }
                .doOnSubscribe { Log.d("TAG", "StartScanning: SCAN") }
                .doOnNext { Log.d("TAG", "ScanningResult: $it") }
                .doOnError { Log.d("TAG", "ScanningError: $it") }
                .doOnDispose { Log.d("TAG", "StopScanning: STOP") }
                .subscribe()
                .also { scanningDisposable = it }
        }
    }

    fun stopScanning(): Completable {
        return Completable.fromAction {
            scanningDisposable?.dispose()
        }
    }

    fun isScanRuntimePermissionGranted(): Single<Boolean> {
        return Single.just(rxBleClient.isScanRuntimePermissionGranted)
    }

}