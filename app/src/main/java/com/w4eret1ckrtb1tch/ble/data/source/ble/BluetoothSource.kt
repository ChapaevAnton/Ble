package com.w4eret1ckrtb1tch.ble.data.source.ble

import android.util.Log
import com.w4eret1ckrtb1tch.ble.data.system.ble.BluetoothAdvertiserService
import com.w4eret1ckrtb1tch.ble.data.system.ble.BluetoothScannerService
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class BluetoothSource(
    private val bluetoothAdvertiserService: BluetoothAdvertiserService,
    private val bluetoothScannerService: BluetoothScannerService
) {

    init {
        Log.d("TAG", "BluetoothSource: $bluetoothAdvertiserService")
    }

    fun startAdvertising(): Completable {
        return bluetoothAdvertiserService.startAdvertising()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun stopAdvertising(): Completable {
        return bluetoothAdvertiserService.stopAdvertising()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun startScanning(): Observable<String> {
        return bluetoothScannerService.isScanRuntimePermissionGranted()
            .filter { it }
            .switchIfEmpty(Single.error(IllegalStateException("Permission not granted at run time")))
            .flatMapObservable {
                bluetoothScannerService.startScanning()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun stopScanning(): Completable {
        return bluetoothScannerService.stopScanning()
    }
}