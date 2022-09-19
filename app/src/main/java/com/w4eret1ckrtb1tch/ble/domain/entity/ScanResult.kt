package com.w4eret1ckrtb1tch.ble.domain.entity

import com.polidea.rxandroidble2.scan.ScanResult

data class ScanResult(
    val serviceUUID: String,
    val userUUID: String,
    val scanResult: ScanResult
)
