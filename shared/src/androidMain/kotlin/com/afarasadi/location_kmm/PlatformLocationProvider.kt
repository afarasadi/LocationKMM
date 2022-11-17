package com.afarasadi.location_kmm

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.afarasadi.location_kmm.model.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

actual fun getPlatformLocationProvider() : LocationProviderContract = PlatformLocationProvider
object PlatformLocationProvider : LocationProviderContract {

    var activity: WeakReference<Activity?> = WeakReference(null)
    private val currentActivity
        get() = activity.get()?.let { activity ->
            if (activity.isFinishing || activity.isDestroyed) null else activity
        }

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val locationData = MutableStateFlow<Location?>(null)

    private var locationProviderClient: WeakReference<FusedLocationProviderClient?> =
        WeakReference(null)

    @get:Synchronized
    @set:Synchronized
    var isConfigured = false

    internal fun configure(activity: Activity) {
        this.activity = WeakReference(activity)
        this.locationProviderClient =
            WeakReference(LocationServices.getFusedLocationProviderClient(activity))
        isConfigured = true
        Log.d(this::class.simpleName, "isConfigured1: $isConfigured")
    }

    @SuppressLint("MissingPermission")
    override fun getLocation(): Flow<Location?> {
        Log.d(this::class.simpleName, "isConfigured2: $isConfigured")

        if (!isConfigured) {
            // warn error etc
            throw RuntimeException("LocationProvider is not configured yet")
        }

        if (currentActivity == null || locationProviderClient.get() == null) {
            // log or throw error
            throw RuntimeException("current activity is null")
        }

        if (isPermissionGranted() == false) {
            throw RuntimeException("Location permission is not granted") // or log this
        }

        val locationRequest = LocationRequest.create().apply {
            interval = 1_000L
            fastestInterval = 500L
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        return callbackFlow {
            Log.d("KMM", "Location start")

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    coroutineScope.launch {
                        send(p0.lastLocation.toKmmLocation())
                        Log.d("KMM", "Location: ${p0.lastLocation.toKmmLocation()}")
                    }
                }
            }

            locationProviderClient.get()
                ?.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

            awaitClose {
                locationProviderClient.get()?.removeLocationUpdates(locationCallback)
            }
        }
    }

    private fun isPermissionGranted(): Boolean? = currentActivity?.let {
        return ActivityCompat.checkSelfPermission(
            it,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun android.location.Location?.toKmmLocation() = this?.let {
        Location(it.latitude, it.longitude)
    }
}
