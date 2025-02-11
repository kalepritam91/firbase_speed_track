package com.example.myapplication.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.LoginResponse
import com.example.myapplication.repo.Repository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val repository = Repository()

    val loginResponse = MutableLiveData<LoginResponse?>()
    val errorMessage = MutableLiveData<String>()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                if (response.isSuccessful && response.body() != null) {
                    loginResponse.postValue(response.body())
                } else {
                    errorMessage.postValue("Login failed: ${response.message()}")
                }
            } catch (e: Exception) {
                errorMessage.postValue("Error: ${e.localizedMessage}")
            }
        }
    }
}
