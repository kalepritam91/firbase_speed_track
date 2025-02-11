package com.example.myapplication.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.TripResponse
import com.example.myapplication.repo.Repository
import kotlinx.coroutines.launch

class TripViewModel : ViewModel() {
    private val repository = Repository()

    val tripResponse = MutableLiveData<TripResponse?>()
    val errorMessage = MutableLiveData<String>()

    fun fetchTripDetails(token: String, userId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getTripDetails(token, userId)
                if (response.isSuccessful && response.body() != null) {
                    tripResponse.postValue(response.body())
                } else {
                    errorMessage.postValue("Failed: ${response.message()}")
                }
            } catch (e: Exception) {
                errorMessage.postValue("Error: ${e.localizedMessage}")
            }
        }
    }

    fun notifyOverSpeed(userId: String, trip_id: String, speed: String) {
        viewModelScope.launch {
            try {
                val response = repository.notifyOverSpeed(userId, trip_id, speed)
                if (response.isSuccessful && response.body() != null) {
                    tripResponse.postValue(response.body())
                } else {
                    errorMessage.postValue("Failed: ${response.message()}")
                }
            } catch (e: Exception) {
                errorMessage.postValue("Error: ${e.localizedMessage}")
            }
        }
    }
}
