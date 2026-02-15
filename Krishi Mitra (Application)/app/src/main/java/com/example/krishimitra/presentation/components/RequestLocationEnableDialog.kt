package com.example.krishimitra.presentation.components

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

@Composable
fun RequestEnableLocationDialog(
    activity: Activity ,
    onEnabled: () -> Unit,
    onDenied: () -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onEnabled()
        } else {
            onDenied()
        }
    }

    fun requestEnableLocation() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000L
        ).setMinUpdateIntervalMillis(5000L).build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val client = LocationServices.getSettingsClient(activity)
        client.checkLocationSettings(builder.build())
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution).build()
                    launcher.launch(intentSenderRequest)
                }
            }
    }

    // Example UI trigger
    Button(onClick = { requestEnableLocation() }) {
        Text("Enable Location")
    }
}
