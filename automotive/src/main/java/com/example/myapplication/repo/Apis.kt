package com.example.myapplication.repo

import com.example.myapplication.model.LoginRequest
import com.example.myapplication.model.LoginResponse
import com.example.myapplication.model.OverSpeedRequest
import com.example.myapplication.model.OverSpeedResponse
import com.example.myapplication.model.TripResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface Apis {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("trip/details")
    suspend fun getTripDetails(
        @Header("Authorization") token: String,  // Pass user auth token
        @Query("userId") userId: String
    ): Response<TripResponse>

    @POST("trip/overSpeed")
    suspend fun notifyOverSpeed(@Body request: OverSpeedRequest
    ): Response<OverSpeedResponse>
}
