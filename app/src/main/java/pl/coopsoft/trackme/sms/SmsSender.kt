package pl.coopsoft.trackme.sms

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.telephony.SmsManager
import android.util.Log
import pl.coopsoft.trackme.Constants

object SmsSender {

    private const val TAG = "SmsSender"
    private const val MIN_TIME = 500L
    private var lastSent = 0L

    private fun shouldSkip() =
        SystemClock.elapsedRealtime() < lastSent + MIN_TIME

    fun sendTextMessage(context: Context, to: String, text: String) {
        if (shouldSkip()) {
            return
        }
        Log.i(TAG, "Sending: $text")
        val sentPendingIntent =
            PendingIntent.getBroadcast(
                context, 0,
                Intent(Constants.BROADCAST_SENT),
                if (Build.VERSION.SDK_INT >= 31) PendingIntent.FLAG_MUTABLE else 0
            )
        val deliveredPendingIntent =
            PendingIntent.getBroadcast(
                context, 0,
                Intent(Constants.BROADCAST_DELIVERED),
                if (Build.VERSION.SDK_INT >= 31) PendingIntent.FLAG_MUTABLE else 0
            )
        lastSent = SystemClock.elapsedRealtime()
        @Suppress("DEPRECATION")
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(to, null, text, sentPendingIntent, deliveredPendingIntent)
    }

}