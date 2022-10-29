package com.afarasadi.location_kmm

import com.afarasadi.location_kmm.model.Location
import kotlinx.coroutines.flow.Flow

interface LocationProviderContract {
    fun isPermissionGranted(): Boolean
    fun requestPermission()
}
