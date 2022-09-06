package pl.coopsoft.trackme.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class RestartAppWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    private companion object {
        private const val TAG = "RestartAppWorker"
    }

    override fun doWork(): Result {
        Log.v(TAG, "Working")
        return Result.success()
    }

}