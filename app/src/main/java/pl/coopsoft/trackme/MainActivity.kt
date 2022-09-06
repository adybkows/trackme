package pl.coopsoft.trackme

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import pl.coopsoft.trackme.utils.Utils

class MainActivity : AppCompatActivity() {
    private companion object {
        private const val PERMISSIONS_REQUEST_1 = 1
        private const val PERMISSIONS_REQUEST_2 = 2
        private const val PERMISSIONS_REQUEST_3 = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        step1SmsPermissions()
    }

    private fun step1SmsPermissions() {
        askForPermissions(
            Utils.getMissingPermissions(this, Utils.smsPermissions),
            PERMISSIONS_REQUEST_1
        ) {
            step2LocationPermissions(false)
        }
    }

    private fun step2LocationPermissions(updated: Boolean) {
        askForPermissions(
            Utils.getMissingPermissions(this, Utils.locationPermissions),
            PERMISSIONS_REQUEST_2
        ) {
            step3BackgroundLocationPermission(updated)
        }
    }

    private fun step3BackgroundLocationPermission(updated: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            askForPermissions(
                Utils.getMissingPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                ),
                PERMISSIONS_REQUEST_3
            ) {
                permissionsGranted(updated)
            }
            return
        }
        permissionsGranted(updated)
    }

    private fun askForPermissions(permissions: List<String>, requestCode: Int, ok: () -> Unit) {
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this, permissions.toTypedArray(), requestCode
            )
        } else {
            ok()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (permissions.filterIndexed { index, _ ->
                grantResults[index] != PackageManager.PERMISSION_GRANTED
            }.isNotEmpty()) {
            permissionsNotGranted()
            return
        }
        when (requestCode) {
            PERMISSIONS_REQUEST_1 -> step2LocationPermissions(true)
            PERMISSIONS_REQUEST_2 -> step3BackgroundLocationPermission(true)
            PERMISSIONS_REQUEST_3 -> permissionsGranted(true)
            else ->
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun permissionsGranted(updated: Boolean) {
        findViewById<TextView>(R.id.text).setText(android.R.string.ok)
        if (updated) {
            Utils.registerSmsReceiver(applicationContext)
        }
        lifecycleScope.launch(Dispatchers.IO) {
            delay(1000)
            MainScope().launch {
                finish()
            }
        }
    }

    private fun permissionsNotGranted() {
        findViewById<TextView>(R.id.text).setText(R.string.not_granted)
    }

}