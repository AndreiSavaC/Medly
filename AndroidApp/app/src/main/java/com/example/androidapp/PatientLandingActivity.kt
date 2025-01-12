package com.example.androidapp

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import java.io.IOException

class PatientLandingActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_landing)

        val sharedPreferences = getSharedPreferences("authPrefs", MODE_PRIVATE)
        val refreshToken = sharedPreferences.getString("REFRESH_TOKEN", null)

        val btnLogout = findViewById<ImageButton>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            performLogout(refreshToken)
        }

        val btnBookAppointment = findViewById<Button>(R.id.btnBookAppointment)
        val btnEditProfile = findViewById<Button>(R.id.btnEditProfile)

        btnBookAppointment.setOnClickListener {
            startActivity(Intent(this, ScheduleActivity::class.java))
        }

        btnEditProfile.setOnClickListener {
            startActivity(Intent(this, MainMenuActivity::class.java))
        }

        val appointmentList = findViewById<LinearLayout>(R.id.appointment_list)
        val appointments = listOf(
            "2024-04-10" to "14:30",
            "2024-04-15" to "09:15"
        )

        for ((date, time) in appointments) {
            val appointmentContainer = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(8, 8, 8, 8)
                setBackgroundColor(getColor(android.R.color.white))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 8, 0, 8)
                }
            }

            val dateTextView = TextView(this).apply {
                text = date
                textSize = 16f
                setTypeface(null, Typeface.BOLD)
                setTextColor(getColor(android.R.color.black))
            }
            appointmentContainer.addView(dateTextView)

            val timeTextView = TextView(this).apply {
                text = "Ora: $time"
                textSize = 14f
                setTextColor(getColor(android.R.color.darker_gray))
            }
            appointmentContainer.addView(timeTextView)

            appointmentList.addView(appointmentContainer)
        }
    }

    private fun performLogout(refreshToken: String?) {
        if (refreshToken.isNullOrEmpty()) {
            val sharedPrefs = getSharedPreferences("authPrefs", MODE_PRIVATE)
            sharedPrefs.edit().clear().apply()

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val url = "http://89.33.44.130:8080/realms/HealthyApp/protocol/openid-connect/logout"

        val formBody = FormBody.Builder()
            .add("client_id", "android-app")
            .add("client_secret", "pHWo9QZW3f8avDCYSN5OSSoMcWCKNeCk")
            .add("refresh_token", refreshToken)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@PatientLandingActivity, "Logout failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(this@PatientLandingActivity, "Logout successful", Toast.LENGTH_LONG).show()
                            val sharedPrefs = getSharedPreferences("authPrefs", MODE_PRIVATE)
                            sharedPrefs.edit().clear().apply()

                            startActivity(Intent(this@PatientLandingActivity, LoginActivity::class.java))
                            finish()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@PatientLandingActivity, "Logout failed: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        })
    }
}