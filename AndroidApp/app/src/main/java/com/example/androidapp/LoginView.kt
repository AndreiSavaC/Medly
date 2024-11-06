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

class LoginView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_view)
        supportActionBar?.hide();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userNameEntry = findViewById<EditText>(R.id.username_input)
        val userPasswordEntry = findViewById<EditText>(R.id.password_input)
        val userLoginButton = findViewById<Button>(R.id.login_button)

        userLoginButton.setOnClickListener{
            val username = userNameEntry.text.toString()
            val pass = userPasswordEntry.text.toString()

            if(username.isEmpty()) {
                Toast.makeText(this,"Empty username is not allowed",Toast.LENGTH_SHORT).show();
            }else if(pass.isEmpty()){
                Toast.makeText(this,"Empty password is not allowed",Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(this,"Login successful",Toast.LENGTH_SHORT).show();
            val intent = Intent(this,UserInfoView::class.java)
            startActivity(intent)
            finish()
        }
    }
}