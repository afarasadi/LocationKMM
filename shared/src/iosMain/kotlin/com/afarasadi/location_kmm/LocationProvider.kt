package com.afarasadi.location_kmm

import com.afarasadi.location_kmm.model.Location
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

actual object KmmLocationProvider {
    private const val DELAY_MILLIS = 1_000L
    actual fun getLocation(): Flow<Location?> {
        return flow {
            delay(DELAY_MILLIS)
            emit(Location(0.0, 0.0))
            delay(DELAY_MILLIS)
            emit(Location(1.0, 1.0))
            delay(DELAY_MILLIS)
            emit(Location(2.0, 2.0))
            delay(DELAY_MILLIS)
            emit(Location(3.0, 3.0))
            delay(DELAY_MILLIS)
            emit(Location(4.0, 4.0))
//            delay(DELAY_MILLIS)
        }
    }

}
