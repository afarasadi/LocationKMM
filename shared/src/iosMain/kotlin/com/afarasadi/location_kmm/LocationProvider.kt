package com.afarasadi.location_kmm

import android.app.Activity
import android.content.Context
import androidx.core.content.ContextCompat
import com.afarasadi.location_kmm.model.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.ref.WeakReference

actual object KmmLocationProvider {
    @get:Synchronized
    var isConfigured = false

    actual fun getLocation(): Flow<Location?> {
        if (!isConfigured) {
            // warn error etc
            return flow {}
        }
    }

}
