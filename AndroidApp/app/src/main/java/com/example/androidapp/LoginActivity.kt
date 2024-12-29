package com.example.androidapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat


class LoginActivity : AppCompatActivity() {

    private fun validateCredentials(email: String, password: String): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_view)
        supportActionBar?.hide()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        WindowInsetsControllerCompat(window, findViewById(R.id.main)).isAppearanceLightStatusBars =
            true


        val loginEmailEntry = findViewById<EditText>(R.id.loginEmailInput)
        val loginPasswordEntry = findViewById<EditText>(R.id.loginPasswordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val createAccountButton = findViewById<Button>(R.id.createAccountButton)

        loginButton.setOnClickListener {
            val email = loginEmailEntry.text.toString()
            val password = loginPasswordEntry.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (validateCredentials(
                        email,
                        password
                    ) && email.contains("@") && password.length > 7
                ) {
                    val intent = Intent(this, MainMenuActivity::class.java)
                    startActivity(intent)
                    finish()
                } else Toast.makeText(
                    this, getString(R.string.invalid_credentials), Toast.LENGTH_SHORT
                ).show()

            } else {
                Toast.makeText(
                    this, getString(R.string.no_credentials), Toast.LENGTH_SHORT
                ).show()
            }
        }

        createAccountButton.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}