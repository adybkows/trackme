package pl.coopsoft.trackme.utils

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Telephony
import androidx.core.content.ContextCompat
import pl.coopsoft.trackme.Constants
import pl.coopsoft.trackme.sms.SmsBroadcastReceiver
import pl.coopsoft.trackme.sms.SmsDeliveredReceiver

object Utils {

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val smsPermissions = arrayOf(
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.SEND_SMS
    )

    private val allRequiredPermissions =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            locationPermissions + smsPermissions + Manifest.permission.ACCESS_BACKGROUND_LOCATION
        } else {
            locationPermissions + smsPermissions
        }

    fun getMissingPermissions(context: Context, permissions: Array<String>) =
        permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }

    fun allPermissionsGranted(context: Context) =
        allRequiredPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    fun registerSmsReceiver(context: Context) {
        val receiver = ComponentName(context, SmsBroadcastReceiver::class.java)
        context.packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        context.registerReceiver(
            SmsBroadcastReceiver(),
            IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION).apply {
                priority = 100
            },
            Manifest.permission.BROADCAST_SMS,
            Handler(Looper.getMainLooper())
        )

        context.registerReceiver(
            SmsDeliveredReceiver(),
            IntentFilter(Constants.BROADCAST_SENT).apply {
                addAction(Constants.BROADCAST_DELIVERED)
            }
        )
    }

}