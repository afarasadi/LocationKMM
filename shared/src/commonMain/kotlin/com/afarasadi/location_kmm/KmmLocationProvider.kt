package com.afarasadi.location_kmm

import com.afarasadi.location_kmm.model.Location
import kotlinx.coroutines.flow.Flow


expect object KmmLocationProvider {
    fun getLocation() : Flow<Location?>
}
