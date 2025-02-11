package com.example.myapplication.view

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.myapplication.CarSpeedService
import com.example.myapplication.model.LoginResponse
import com.example.myapplication.model.TripResponse
import com.example.myapplication.view_model.LoginViewModel
import com.example.myapplication.view_model.TripViewModel
import kotlin.coroutines.jvm.internal.CompletedContinuation.context

class TripActivity : AppCompatActivity() {

    private val mTripViewModel: TripViewModel by viewModels()
    var mCurrentTripResponse: TripResponse? = null
    var mLoginResponse: LoginResponse? = null
    var mCarSpeedService: CarSpeedService? = null
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip)
        val tripInfoText = findViewById<TextView>(R.id.tripInfoText)
        val fetchTripButton = findViewById<Button>(R.id.fetchTripButton)
        mCarSpeedService = CarSpeedService(applicationContext)
        mCarSpeedService!!.startListening()
        mLoginResponse = loginViewModel.loginResponse

        val userToken = mLoginResponse!!.token  // Replace with actual token
        val userId = mLoginResponse!!.id // Replace with actual user ID

        fetchTripButton.setOnClickListener {
            if (userToken != null) {
                mTripViewModel.fetchTripDetails(userToken, userId)
            }
        }

        mTripViewModel.tripResponse.observe(this, Observer { response ->
            response?.let {
                tripInfoText.text = """
                    Trip ID: ${it.tripId}
                    Start: ${it.startLocation}
                    End: ${it.endLocation}
                    Distance: ${it.distance} km
                    Duration: ${it.duration} mins
                    Avg Speed: ${it.averageSpeed} km/h
                    Speed Limit: ${it.speedLimit} km/h
                """.trimIndent()
                mCurrentTripResponse = response
                mCarSpeedService!!.setSpeedLimit(response.speedLimit)

            }
        })

        mTripViewModel.errorMessage.observe(this, Observer { error ->
            tripInfoText.text = error
        })
    }

    // This API will call from carSpeedService
    fun notifyOverSpeed(speedKmh: Int) {
        val tripid = mCurrentTripResponse?.tripId
        val userId = mCurrentTripResponse?.userid
        if (tripid != null && userId != null)
            mTripViewModel.notifyOverSpeed(tripid, userId, speedKmh.toString())
        // update local notification
        showWarningAlert()
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
}
