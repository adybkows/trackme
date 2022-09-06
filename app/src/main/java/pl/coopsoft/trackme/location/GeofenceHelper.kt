package pl.coopsoft.trackme.location

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class GeofenceHelper(applicationContext: Context) {

    private companion object {
        private const val TAG = "GeofenceHelper"
        private const val REQUEST_ID = "1"
        private const val GEOFENCE_RADIUS_IN_METERS = 2.0f
    }

    private val geofencingClient = LocationServices.getGeofencingClient(applicationContext)

    private val broadcastReceiverIntent = Intent(applicationContext, GeofenceBroadcastReceiver::class.java)
        .apply { action = GeofenceBroadcastReceiver.ACTION_GEOFENCE_EVENT }
    private val pendingIntent =
        if (Build.VERSION.SDK_INT >= 31)
            PendingIntent.getBroadcast(applicationContext, 0, broadcastReceiverIntent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        else
            PendingIntent.getBroadcast(applicationContext, 0, broadcastReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    fun setup(lat: Double, lon: Double) {
        if (lat == 0.0 && lon == 0.0) {
            return
        }
        val geofence = Geofence.Builder()
            .setRequestId(REQUEST_ID)
            .setCircularRegion(lat, lon, GEOFENCE_RADIUS_IN_METERS)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT)
            .addGeofence(geofence)
            .build()

        geofencingClient.removeGeofences(listOf(REQUEST_ID)).run {
            addOnCompleteListener {
                try {
                    geofencingClient.addGeofences(request, pendingIntent).run {
                        addOnSuccessListener {
                        }
                        addOnFailureListener {
                            Log.e(TAG, "Cannot add GPS geofence: $it")
                        }
                    }
                } catch (e: SecurityException) {
                    Log.e(TAG, "Cannot add GPS geofence: $e")
                }
            }
        }
    }

    fun shutdown() {
        geofencingClient.removeGeofences(listOf(REQUEST_ID)).run {
            addOnCompleteListener {
            }
        }
    }
}