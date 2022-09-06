package pl.coopsoft.trackme.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import pl.coopsoft.trackme.Constants

class SmsDeliveredReceiver: BroadcastReceiver() {

    private companion object {
        private const val TAG = "SmsDeliveredReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Constants.BROADCAST_SENT ->
                Log.v(TAG, "Text message sent")

            Constants.BROADCAST_DELIVERED ->
                Log.v(TAG, "Text message delivered")
        }
    }

}