package pl.coopsoft.trackme.location

import android.content.Context
import android.content.IntentSender
import android.os.Looper
import android.util.Log
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import pl.coopsoft.trackme.Globals

class GpsListener(applicationContext: Context, normalInterval: Long, fastInterval: Long) {

    private companion object {
        private const val TAG = "GpsListener"
    }

    private var requestingLocation = false
    private val mFusedLocationClient =
        LocationServices.getFusedLocationProviderClient(applicationContext)

    private val mSettingsClient = LocationServices.getSettingsClient(applicationContext)

    private val mLocationRequest = LocationRequest.create().apply {
        interval = normalInterval
        fastestInterval = fastInterval
        @Suppress("DEPRECATION")
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        smallestDisplacement = 1.0f
    }

    private val mLocationSettingsRequest = LocationSettingsRequest.Builder()
        .addLocationRequest(mLocationRequest).build()

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            Globals.updateLocation(applicationContext, locationResult.locations.last())
        }
    }

    fun startRequestLocationUpdate() {
        Log.i(TAG, "startRequestLocationUpdate")
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
            .addOnSuccessListener {
                try {
                    mFusedLocationClient.requestLocationUpdates(
                        mLocationRequest, mLocationCallback, Looper.getMainLooper()
                    )
                    requestingLocation = true
                } catch (e: SecurityException) {
                    Log.e(TAG, "startRequestLocationUpdate error $e")
                    e.printStackTrace()
                    Globals.clearGpsData()
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "startRequestLocationUpdate error $e")
                e.printStackTrace()
                Globals.clearGpsData()
                if (e is ResolvableApiException) {
                    try {
                        //e.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS_GPS)
                    } catch (sie: IntentSender.SendIntentException) {
                    }
                } else {
                    Log.e(TAG, "Wrong settings")
                }
            }
    }

    fun stopLocationUpdates() {
        Log.i(TAG, "stopLocationUpdates")
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
        Globals.geofenceHelper?.shutdown()
        Globals.geofenceHelper = null
        requestingLocation = false
    }

    fun locationInProgress() = requestingLocation
}