package com.afarasadi.location_kmm

import com.afarasadi.location_kmm.model.Location
import kotlinx.coroutines.flow.Flow


class KmmLocationProvider constructor(
    internal val platformLocationProvider: LocationProviderContract = getPlatformLocationProvider()
) {
    companion object
    fun getLocation() : Flow<Location?> = platformLocationProvider.getLocation()
}
