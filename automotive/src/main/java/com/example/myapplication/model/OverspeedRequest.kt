package com.example.myapplication.model

data class OverSpeedRequest(
    val user_id: String,
    val trip_id: String,
    var speed: String
)

data class OverSpeedResponse(
    val success: Boolean,
    val message: String,
    val token: String?
)
