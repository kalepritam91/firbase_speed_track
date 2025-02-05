package com.example.firbasespeed

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.LocationRequest
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.firbasespeed.ui.theme.FirbasespeedTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.coroutines.jvm.internal.CompletedContinuation.context

class MainActivity : ComponentActivity() {
    var speed = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FirbasespeedTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        askNotificationPermission()
        FirebaseApp.initializeApp(this)
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)
        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.setInterval(1000) // Update every second
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

// Start location updates
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )


    }

    val locationCallback: LocationCallback = object : LocationCallback() {
        fun onLocationResult(locationResult: LocationResult?) {
            if (locationResult != null && locationResult.getLocations() != null) {
                for (location in locationResult.getLocations()) {
                    speed = location.getSpeed() // Speed in meters per second
                    checkSpeedLimit(speed)
                }
            }
        }
    }

    private fun checkSpeedLimit(speed: Int) {
        val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("customers")
        val customerId = "customer1" // This could be dynamically set based on logged-in user

        database.child(customerId).child("speedLimit").get().addOnCompleteListener { task ->
            if (task.isSuccessful()) {
                val speedLimit: Long =
                    task.getResult().getValue(Long::class.java) // Speed limit from DB
                if (speed > speedLimit) {
                    sendNotification() // Send notification if speed exceeds limit
                    showWarningAlert() // Show warning alert in the app
                }
            }
        }

    }

    private fun sendNotification() {
        FirebaseMessaging.getInstance().send(
            Builder("your_server_key@fcm.googleapis.com")
                .setMessageId(messageId.toString())
                .addData("title", "Speed Limit Exceeded")
                .addData("body", "The customer has exceeded the speed limit.")
                .build()
        )
    }

    private fun showWarningAlert() {
        AlertDialog.Builder(context)
            .setTitle("Speed Limit Exceeded")
            .setMessage("You are driving above the speed limit. Please slow down.")
            .setPositiveButton(
                "OK"
            ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
            .setCancelable(false)
            .show()
    }


    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FirbasespeedTheme {
        Greeting("Android")
    }

}