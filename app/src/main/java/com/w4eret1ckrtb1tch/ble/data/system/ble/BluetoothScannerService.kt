package com.w4eret1ckrtb1tch.ble.data.system.ble

import android.util.Log
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanResult
import com.polidea.rxandroidble2.scan.ScanSettings
import com.w4eret1ckrtb1tch.ble.domain.entity.ScanningResult
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class BluetoothScannerService(
    private val rxBleClient: RxBleClient
) {

    private var scanningDisposable: Disposable? = null

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
        .build()

    private val scanFilter = ScanFilter.Builder()
        .setServiceUuid(BluetoothAdvertiserService.SERVICE_UUID)
        .build()

    // TODO: Возможно нужен горячий Observable, и подписка на него...
    fun startScanning(isUserDistinct: Boolean = false): Observable<ScanningResult> {
        return Observable.create { emitter ->

            rxBleClient
                .scanBleDevices(scanSettings, scanFilter)
                .scanTimeBle(10L, TimeUnit.SECONDS)
                .distinctBle(isUserDistinct)
                .map { scanResult ->
                    ScanningResult(
                        serviceUUID = scanResult.scanRecord.serviceUuids[SERVICE_INDEX].toString(),
                        userUUID = scanResult.scanRecord.serviceUuids[USER_INDEX].toString(),
                        scanResult = scanResult
                    )
                }
                .doOnSubscribe { Log.d("TAG", "ScannerService: START") }
                .doOnDispose { Log.d("TAG", "ScannerService: doOnDispose") }
                .doFinally { Log.d("TAG", "ScannerService: STOP") }
                .subscribe(emitter::onNext, emitter::onError)
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

    // QUESTION: общая продолжительность сканирования вне зависимости от результата
    private fun <T : ScanResult> Observable<T>.scanTimeBle(
        time: Long,
        unit: TimeUnit
    ): Observable<T> {
        val stopTime = System.currentTimeMillis() + unit.toMillis(time)
        return this.timestamp()
            .map { timed ->
                if (timed.time() >= stopTime) {
                    scanningDisposable?.dispose()
                }
                timed.value()
            }
    }

    // QUESTION: отбрасывает повторяющиеся USER_UUID
    private fun <T : ScanResult> Observable<T>.distinctBle(isEnabled: Boolean): Observable<T> {
        return if (isEnabled) this.distinct { it.scanRecord.serviceUuids[USER_INDEX].toString() } else this
    }

    private companion object {
        const val SERVICE_INDEX = 0
        const val USER_INDEX = 1
    }
}