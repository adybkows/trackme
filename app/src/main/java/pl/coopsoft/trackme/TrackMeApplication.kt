package pl.coopsoft.trackme

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import pl.coopsoft.trackme.utils.Utils
import pl.coopsoft.trackme.workers.RestartAppWorker
import java.util.concurrent.TimeUnit

class TrackMeApplication : Application() {

    private companion object {
        private const val WORK_MANAGER_TAG = "WorkManagerTag"
    }

    override fun onCreate() {
        super.onCreate()
        if (Utils.allPermissionsGranted(this)) {
            Utils.registerSmsReceiver(this)
        }

        val workRequest = PeriodicWorkRequestBuilder<RestartAppWorker>(15, TimeUnit.MINUTES)
            .setInitialDelay(10, TimeUnit.SECONDS)
            .addTag(WORK_MANAGER_TAG)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            WORK_MANAGER_TAG, ExistingPeriodicWorkPolicy.REPLACE, workRequest
        )
    }

}
