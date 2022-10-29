plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.afarasadi.location_kmm.android"
    compileSdk = 33
    defaultConfig {
        applicationId = "com.afarasadi.location_kmm.android"
        minSdk = 23
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(project(":shared"))


    val composeBom = platform("androidx.compose:compose-bom:2022.10.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material:material")


    implementation("androidx.activity:activity-compose:1.6.1")
}
