package com.w4eret1ckrtb1tch.ble.ui.ble

import android.util.Log
import com.w4eret1ckrtb1tch.ble.domain.usecase.StartAdvertisingUseCase
import com.w4eret1ckrtb1tch.ble.domain.usecase.StartScanningUseCase
import com.w4eret1ckrtb1tch.ble.domain.usecase.StopAdvertisingUseCase
import com.w4eret1ckrtb1tch.ble.ui.common.ScreenPm
import io.reactivex.disposables.Disposable
import me.dmdev.rxpm.action

class BlePm(
    private val value: String,
    private val startAdvertisingUseCase: StartAdvertisingUseCase,
    private val stopAdvertisingUseCase: StopAdvertisingUseCase,
    private val startScanningUseCase: StartScanningUseCase
) : ScreenPm() {

    private var scanDisposable: Disposable? = null

    val startAdvertisingClick = action<Unit>()
    val stopAdvertisingClick = action<Unit>()
    val startScanningClick = action<Unit>()
    val stopScanningClick = action<Unit>()

    override fun onCreate() {
        super.onCreate()

        Log.d("TAG", "onCreate: $startAdvertisingUseCase")
        Log.d("TAG", "onCreate: $stopAdvertisingUseCase")

        startAdvertisingClick.observable
            .switchMapCompletable {
                startAdvertisingUseCase()
                    .doOnComplete { Log.d("TAG", "StartAdvertising: OK") }
                    .doOnError { Log.d("TAG", "StartAdvertising: ERROR ->$it") }
            }
            .retry()
            .subscribe()
            .untilDestroy()

        stopAdvertisingClick.observable
            .switchMapCompletable {
                stopAdvertisingUseCase()
                    .doOnComplete { Log.d("TAG", "StopAdvertising: OK") }
                    .doOnError { Log.d("TAG", "StopAdvertising: ERROR ->$it") }
            }
            .retry()
            .subscribe()
            .untilDestroy()

        startScanningClick.observable
            .retry()
            .subscribe {
                startScanningUseCase()
                    .doOnSubscribe { Log.d("TAG", "StartScanning: SCAN") }
                    .doOnNext { Log.d("TAG", "ScanningResult: $it") }
                    .doOnError { Log.d("TAG", "ScanningError: $it") }
                    .doOnDispose { Log.d("TAG", "StopScanning: STOP") }
                    .subscribe()
                    .let { scanDisposable = it }
            }
            .untilDestroy()

        stopScanningClick.observable
            .retry()
            .subscribe {
                scanDisposable?.dispose()
            }.untilDestroy()
    }
}