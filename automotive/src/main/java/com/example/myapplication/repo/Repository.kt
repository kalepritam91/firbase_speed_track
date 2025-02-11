package com.example.myapplication.repo

import com.example.myapplication.model.LoginRequest
import com.example.myapplication.model.LoginResponse
import com.example.myapplication.model.OverSpeedRequest
import com.example.myapplication.model.OverSpeedResponse
import com.example.myapplication.model.TripResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Repository {
    private val api: Apis

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://your-api-url.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(Apis::class.java)
    }

    suspend fun login(email: String, password: String): Response<LoginResponse> {
        return api.login(LoginRequest(email, password))
    }

    suspend fun getTripDetails(token: String, userId: String): Response<TripResponse> {
        return api.getTripDetails(token, userId)
    }

    suspend fun notifyOverSpeed(userId: String, trip_id: String, speed: String): Response<OverSpeedResponse> {
        return api.notifyOverSpeed(OverSpeedRequest( userId, trip_id,speed))
    }
}
