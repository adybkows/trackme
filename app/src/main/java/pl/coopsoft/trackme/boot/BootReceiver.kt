package pl.coopsoft.trackme.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import pl.coopsoft.trackme.MainActivity
import pl.coopsoft.trackme.utils.Utils

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            if (!Utils.allPermissionsGranted(context)) {
                context.startActivity(Intent(context, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
        }
    }

}