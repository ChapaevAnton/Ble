package com.w4eret1ckrtb1tch.ble.ui.ble

import android.util.Log
import com.w4eret1ckrtb1tch.ble.domain.usecase.StartAdvertisingUseCase
import com.w4eret1ckrtb1tch.ble.domain.usecase.StartScanningUseCase
import com.w4eret1ckrtb1tch.ble.domain.usecase.StopAdvertisingUseCase
import com.w4eret1ckrtb1tch.ble.ui.common.ScreenPm
import me.dmdev.rxpm.action

class BlePm(
    private val value: String,
    private val startAdvertisingUseCase: StartAdvertisingUseCase,
    private val stopAdvertisingUseCase: StopAdvertisingUseCase,
    private val startScanningUseCase: StartScanningUseCase
) : ScreenPm() {

    val startAdvertisingClick = action<Unit>()
    val stopAdvertisingClick = action<Unit>()
    val startScanningClick = action<Unit>()

    override fun onCreate() {
        super.onCreate()

        Log.d("TAG", "onCreate: $startAdvertisingUseCase")
        Log.d("TAG", "onCreate: $stopAdvertisingUseCase")

        startAdvertisingClick.observable
            .switchMapCompletable {
                startAdvertisingUseCase()
                    .doOnComplete { Log.d("TAG", "startAdvertisingClick: ok") }
                    .doOnError { Log.d("TAG", "startAdvertisingClick: error") }
            }
            .retry()
            .subscribe()
            .untilDestroy()

        stopAdvertisingClick.observable
            .switchMapCompletable {
                stopAdvertisingUseCase()
                    .doOnComplete { Log.d("TAG", "stopAdvertisingClick: ok") }
                    .doOnError { Log.d("TAG", "stopAdvertisingClick: error") }
            }
            .retry()
            .subscribe()
            .untilDestroy()

        // TODO: нужно добавить запрос permission
        startScanningClick.observable
            .switchMap {
                Log.d("TAG", "startScanningClick: SCAN")
                startScanningUseCase()
                    .doOnNext { Log.d("TAG", "ScanningResult: $it") }
                    .doOnComplete { Log.d("TAG", "startScanningClick: ok") }
                    .doOnError { Log.d("TAG", "startScanningClick: $it") }
            }
            .retry()
            .subscribe()
            .untilDestroy()
    }
}