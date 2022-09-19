package com.w4eret1ckrtb1tch.ble.domain.entity

import android.os.ParcelUuid
import android.util.Log
import java.util.UUID

object UserConstants {

    val USER_UUID: ParcelUuid = ParcelUuid.fromString(UUID.randomUUID().toString())
        .also { Log.d("TAG", "USER_ID: ${it.uuid}") }
}