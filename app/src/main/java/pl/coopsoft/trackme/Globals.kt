package pl.coopsoft.trackme

import android.content.Context
import android.location.Location
import android.os.SystemClock
import android.util.Log
import pl.coopsoft.trackme.location.GeofenceHelper
import pl.coopsoft.trackme.location.GpsService
import pl.coopsoft.trackme.sms.SmsSender

object Globals {

    var geofenceHelper: GeofenceHelper? = null
    var recipient: String? = null

    var latitude = 0.0
    var longitude = 0.0
    var altitude = 0.0
    var speed = 0.0f
    var accuracy = 0f
    var timestamp = 0L

    fun clearGpsData() {
        Log.d("GPS", "clearGpsData")
        latitude = 0.0
        longitude = 0.0
        altitude = 0.0
        speed = 0.0f
        accuracy = 0f
        timestamp = 0L
    }

    fun updateLocation(context: Context, loc: Location) {
        val now = SystemClock.elapsedRealtime()
        val diff =
            if (timestamp != 0L) "timediff=${(now - timestamp + 500L) / 1000L}s" else ""

        latitude = loc.latitude
        longitude = loc.longitude
        altitude = loc.altitude
        accuracy = loc.accuracy
        speed = loc.speed
        timestamp = now

        Log.i(
            "GPS",
            "Location: lat=$latitude lon=$longitude alt=$altitude accuracy=$accuracy speed=$speed $diff"
        )

        //geofenceHelper?.setup(loc.latitude, loc.longitude)

        recipient?.let {
            val message =
                "https://www.google.com/maps/search/?api=1&query=${latitude}%2C${longitude}"
            SystemClock.sleep(500)
            SmsSender.sendTextMessage(context, it, message)
        }

        GpsService.stop(context)
    }
}
