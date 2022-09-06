package pl.coopsoft.trackme.location

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import pl.coopsoft.trackme.Globals
import pl.coopsoft.trackme.utils.Notifications

class GpsService : Service() {

    companion object {
        private const val TAG = "GpsService"
        private const val NOTIFICATION_ID_NUMBER = 1
        private var myService: GpsService? = null

        private val serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Log.d(TAG, "onServiceConnected")
                myService = (service as LocalBinder).getService()
                myService?.startRequestLocationUpdate()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                Log.d(TAG, "onServiceDisconnected")
                myService = null
            }
        }

        fun start(context: Context) {
            Log.d(TAG, "start")
            context.applicationContext.bindService(
                Intent(context, GpsService::class.java),
                serviceConnection,
                BIND_AUTO_CREATE
            )
        }

        fun stop(context: Context) {
            Log.d(TAG, "stop")
            myService?.stopSelf()
            context.applicationContext.unbindService(serviceConnection)
        }

        fun startRequestLocationUpdate() {
            Log.d(TAG, "startRequestLocationUpdate")
            myService?.startRequestLocationUpdate()
        }

        fun stopLocationUpdates() {
            Log.d(TAG, "stopLocationUpdates")
            myService?.stopLocationUpdates()
        }

        fun locationInProgress() = myService?.locationInProgress() ?: false
    }

    private val binder = LocalBinder()
    private lateinit var gpsListener: GpsListener

    inner class LocalBinder : Binder() {
        fun getService() = this@GpsService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        Globals.geofenceHelper = GeofenceHelper(applicationContext)
        gpsListener = GpsListener(applicationContext, 10000, 30000)
    }

    override fun onDestroy() {
        stopLocationUpdates()
    }

    private fun startRequestLocationUpdate() {
        Notifications.createNotification(this)
        Notifications.showAsForegroundService(this, NOTIFICATION_ID_NUMBER)
        gpsListener.startRequestLocationUpdate()
    }

    private fun stopLocationUpdates() {
        gpsListener.stopLocationUpdates()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
    }

    private fun locationInProgress() = gpsListener.locationInProgress()
}