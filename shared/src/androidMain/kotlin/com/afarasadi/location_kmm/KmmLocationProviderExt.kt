package com.afarasadi.location_kmm

import android.app.Activity

fun KmmLocationProvider.configureActivity(activity: Activity) {
    (this.platformLocationProvider as? PlatformLocationProvider)?.configure(activity)
}
