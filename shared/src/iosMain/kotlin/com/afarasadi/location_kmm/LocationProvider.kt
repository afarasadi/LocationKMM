package com.afarasadi.location_kmm

import com.afarasadi.location_kmm.model.Location
import kotlinx.cinterop.useContents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import platform.CoreLocation.CLLocationCoordinate2D
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLLocationAccuracyBestForNavigation
import platform.darwin.NSObject

actual object KmmLocationProvider {

    private val locationManager: CLLocationManager by lazy {
        val manager = CLLocationManager().apply {
            desiredAccuracy = kCLLocationAccuracyBestForNavigation
            pausesLocationUpdatesAutomatically = false
        }
        manager
    }

    private val locationManagerDelegate by lazy {
        object : NSObject(), CLLocationManagerDelegateProtocol {
            private var onLocationUpdate: (Location?) -> Unit = {}

            fun setOnLocationUpdate(block: (Location?) -> Unit) {
                onLocationUpdate = block
            }

            override fun locationManager(
                manager: CLLocationManager,
                didUpdateLocations: List<*>,
            ) {
                coroutineScope.launch {
                    manager.location?.coordinate?.useContents {
                        onLocationUpdate.invoke(this.toKmmLocation())
                    }
                }
            }
        }
    }

    private val locationDataChannelFlow = channelFlow<Location?> { }

    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    actual fun getLocation(): Flow<Location?> {
        locationManager.requestAlwaysAuthorization()

        return callbackFlow {
            locationManagerDelegate.setOnLocationUpdate { location ->
                coroutineScope.launch {
                    send(location)
                }
            }
            locationManager.setDelegate(locationManagerDelegate)
            locationManager.startUpdatingLocation()
            locationManager.startUpdatingHeading()

            awaitClose {
                locationManager.stopUpdatingLocation()
            }
        }
    }

    private fun CLLocationCoordinate2D.toKmmLocation() = Location(latitude = latitude, longitude)

    private const val DELAY_MILLIS = 1_000L
}
