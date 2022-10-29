package com.afarasadi.location_kmm.android

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.afarasadi.location_kmm.KmmLocationProvider
import com.afarasadi.location_kmm.configureActivity
import com.afarasadi.location_kmm.model.Location
import kotlinx.coroutines.flow.flow

class MainActivity : ComponentActivity() {

    private val permissions = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KmmLocationProvider.configureActivity(this)

        setContent {
            var isPermissionsGranted by remember { mutableStateOf(this.isPermissionGranted() == true) }
            var locationFlow by remember { mutableStateOf(flow<Location?> {}) }
            val location by locationFlow.collectAsState(initial = null)

            LaunchedEffect(isPermissionsGranted) {
                if (isPermissionsGranted) {
                    locationFlow = KmmLocationProvider.getLocation()
                }
            }

            val activityResultLauncher =
                rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) { results ->
                    isPermissionsGranted = results.values.all { granted -> granted }
                }


            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(18.dp), contentAlignment = Alignment.Center
                    ) {
                        if (!isPermissionsGranted) {
                            Button(
                                onClick = {
                                    activityResultLauncher.launch(permissions)
                                }
                            ) {
                                Text("Grant permission")
                            }
                        } else {
                            LocationScreen(location)
                        }
                    }
                }
            }
        }
    }

    private fun isPermissionGranted(): Boolean? = ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED


}

@Composable
fun LocationScreen(location: Location? = null) {
    if (location == null) {
        Text("Loading . . . ")
        return
    }
    Text("$location")
}

@Composable
fun Greeting(text: String) {
    Text(text = text)
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        Greeting("Hello, Android!")
    }
}
