package com.example.krishimitra.data.repo

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.Geocoder
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.krishimitra.domain.model.location.Location
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationManager @Inject
constructor(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient,

    ) {
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend fun getLocation(): Location? {
        val geocoder = Geocoder(context)

        return suspendCancellableCoroutine { cont ->
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location == null) {
                    // Request a fresh location update
                    val locationRequest = LocationRequest.Builder(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        2000L
                    ).setMinUpdateIntervalMillis(1000L).build()

                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        object : LocationCallback() {
                            override fun onLocationResult(result: LocationResult) {
                                fusedLocationClient.removeLocationUpdates(this)
                                val freshLocation = result.lastLocation
                                if (freshLocation != null) {
                                    try {
                                        val addresses = geocoder.getFromLocation(
                                            freshLocation.latitude,
                                            freshLocation.longitude,
                                            1
                                        )
                                        if (!addresses.isNullOrEmpty()) {
                                            val address = addresses[0]
                                            cont.resume(
                                                Location(
                                                    village = address.subLocality
                                                        ?: address.locality ?: "",
                                                    state = address.adminArea ?: "",
                                                    district = address.subAdminArea ?: "",
                                                    pinCode = address.postalCode ?: "",
                                                    latitude = freshLocation.latitude,
                                                    longitude = freshLocation.longitude
                                                )
                                            )
                                        } else {
                                            cont.resume(null)
                                        }
                                    } catch (e: Exception) {
                                        cont.resumeWithException(e)
                                    }
                                } else {
                                    cont.resume(null)
                                }
                            }
                        },
                        Looper.getMainLooper()
                    )
                } else {
                    // Cached location is available
                    try {
                        val addresses =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (!addresses.isNullOrEmpty()) {
                            val address = addresses[0]
                            cont.resume(
                                Location(
                                    village = address.subLocality ?: address.locality ?: "",
                                    state = address.adminArea ?: "",
                                    district = address.subAdminArea ?: "",
                                    pinCode = address.postalCode ?: "",
                                    latitude = location.latitude,
                                    longitude = location.longitude
                                )
                            )
                        } else {
                            cont.resume(null)
                        }
                    } catch (e: Exception) {
                        cont.resumeWithException(e)
                    }
                }
            }.addOnFailureListener { e ->
                cont.resumeWithException(e)
            }
        }
    }


    companion object {
        fun isLocationEnabled(context: Context): Boolean {
            val locationserviceManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationserviceManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationserviceManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
    }

    fun requestEnableLocation(activity: Activity) {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000L // update interval in ms
        ).setMinUpdateIntervalMillis(5000L) // fastest interval
            .build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true) // always show the system dialog if needed

        val client = LocationServices.getSettingsClient(activity)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            Log.d("Location", "Location already enabled")
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(activity, 1001)
                } catch (_: IntentSender.SendIntentException) {
                }
            }
        }
    }
}