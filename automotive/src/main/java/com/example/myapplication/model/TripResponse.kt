package com.example.myapplication.model

data class TripResponse(
    val userid:String,
    val tripId: String,
    val startLocation: String,
    val endLocation: String,
    val distance: Double,  // in kilometers
    val duration: Double,  // in minutes
    val averageSpeed: Double,  // in km/h
    val speedLimit: Double,  // in km/h
)
