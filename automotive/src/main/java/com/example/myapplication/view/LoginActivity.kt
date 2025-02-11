package com.example.myapplication.view

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.myapplication.view_model.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val statusTextView = findViewById<TextView>(R.id.statusTextView)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            loginViewModel.login(email, password)
        }

        loginViewModel.loginResponse.observe(this, Observer { response ->
            response?.let {
                statusTextView.text = if (it.success) "Login Successful" else "Login Failed"
                if (it.success) {
                    val intent = Intent(this, TripActivity::class.java)
                    startActivity(intent)
                }
            }
        })

        loginViewModel.errorMessage.observe(this, Observer { error ->
            statusTextView.text = error
        })
    }
}
