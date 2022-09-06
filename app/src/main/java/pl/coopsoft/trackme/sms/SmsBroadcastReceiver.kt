package pl.coopsoft.trackme.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import pl.coopsoft.trackme.Globals
import pl.coopsoft.trackme.location.GpsService
import java.util.regex.Pattern

class SmsBroadcastReceiver : BroadcastReceiver() {

    private companion object {
        private const val TAG = "SmsBroadcastReceiver"
        private val pattern = Pattern.compile(
            "gdzie jeste[sś]\\??",
            Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE
        )
    }

    override fun onReceive(context: Context, intent: Intent) {
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        messages.forEach {
            Log.i(TAG, "${it.originatingAddress}  ${it.messageBody}")
            if (pattern.matcher(it.messageBody).matches()) {
                if (!it.originatingAddress.isNullOrEmpty()) {
                    Globals.recipient = it.originatingAddress
                    SmsSender.sendTextMessage(context, it.originatingAddress.orEmpty(), "...")
                    GpsService.start(context)
                }
            }
        }
    }
}