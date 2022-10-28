package com.afarasadi.location_kmm

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform