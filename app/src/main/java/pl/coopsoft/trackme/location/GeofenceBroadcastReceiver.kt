package pl.coopsoft.trackme.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import pl.coopsoft.trackme.Globals

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "GeofenceBroadcastRecv"

        internal const val ACTION_GEOFENCE_EVENT =
            "pl.mindmade.pim.gps.action.ACTION_GEOFENCE_EVENT"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.v(TAG, "${intent.action}")

        if (intent.action == ACTION_GEOFENCE_EVENT) {
            GeofencingEvent.fromIntent(intent)?.let { geofencingEvent ->
                if (geofencingEvent.hasError()) {
                    val errorMessage =
                        GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
                    Log.e(TAG, "Geofence receiver error $errorMessage")
                } else {
                    geofencingEvent.triggeringLocation?.let { triggeringLocation ->
                        Globals.updateLocation(context, triggeringLocation)
                    }
                }
            }
        }
    }
}