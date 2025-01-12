package com.example.androidapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.auth0.android.jwt.JWT
import com.example.androidapp.api.RetrofitClient
import com.example.androidapp.models.UserResponse
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private val client = OkHttpClient()

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
        WindowInsetsControllerCompat(window, findViewById(R.id.main)).isAppearanceLightStatusBars = true

        val sharedPrefs = getSharedPreferences("authPrefs", MODE_PRIVATE)
        val accessToken = sharedPrefs.getString("ACCESS_TOKEN", null)
        val isDoctor = sharedPrefs.getBoolean("IS_DOCTOR", false)

        if (!accessToken.isNullOrEmpty()) {
            val jwt = try {
                JWT(accessToken)
            } catch (e: Exception) {
                null
            }

            if (jwt != null && !isTokenExpired(jwt)) {
                if (isDoctor) {
                    startActivity(Intent(this, DoctorLandingActivity::class.java))
                } else {
                    startActivity(Intent(this, PatientLandingActivity::class.java))
                }
                finish()
                return
            } else {
                sharedPrefs.edit().clear().apply()
            }
        }

        val loginEmailEntry = findViewById<EditText>(R.id.loginEmailInput)
        val loginPasswordEntry = findViewById<EditText>(R.id.loginPasswordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val createAccountButton = findViewById<Button>(R.id.createAccountButton)

        loginButton.setOnClickListener {
            val email = loginEmailEntry.text.toString()
            val password = loginPasswordEntry.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (email.contains("@") && password.length > 5) {
                    performLogin(email, password)
                } else {
                    Toast.makeText(this, getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, getString(R.string.no_credentials), Toast.LENGTH_SHORT).show()
            }
        }

        createAccountButton.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun isTokenExpired(jwt: JWT): Boolean {
        val expiresAt = jwt.expiresAt
        return (expiresAt == null || expiresAt.time < System.currentTimeMillis())
    }

    private fun performLogin(username: String, password: String) {
        val url = "http://89.33.44.130:8080/realms/HealthyApp/protocol/openid-connect/token"

        val formBody = FormBody.Builder()
            .add("client_id", "android-app")
            .add("client_secret", "pHWo9QZW3f8avDCYSN5OSSoMcWCKNeCk")
            .add("grant_type", "password")
            .add("username", username)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Login failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use { resp ->
                    if (resp.isSuccessful) {
                        val responseBody = resp.body?.string()
                        val json = JSONObject(responseBody ?: "")
                        val accessToken = json.optString("access_token", "")
                        val refreshToken = json.optString("refresh_token", "")

                        val keycloakUserId = getKeycloakUserIdFromToken(accessToken)
                        Log.d("LoginActivityLog", "Keycloak user ID: $keycloakUserId")

                        if (keycloakUserId == null || accessToken.isEmpty()) {
                            runOnUiThread {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Authentication failed. Please try again.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            return
                        }

                        val callUser = RetrofitClient.userService.getUserByKeycloakId(keycloakUserId)
                        callUser.enqueue(object : retrofit2.Callback<UserResponse> {
                            override fun onResponse(
                                call: retrofit2.Call<UserResponse>,
                                response: retrofit2.Response<UserResponse>
                            ) {
                                if (response.isSuccessful) {
                                    val userResponse = response.body()
                                    Log.d("LoginActivityLog", "User : $userResponse")

                                    if (userResponse != null) {
                                        val isDoctor = userResponse.isDoctor
                                        val patientId = userResponse.id

                                        val sharedPrefs = getSharedPreferences("authPrefs", MODE_PRIVATE)
                                        sharedPrefs.edit().apply {
                                            putString("ACCESS_TOKEN", accessToken)
                                            putString("REFRESH_TOKEN", refreshToken)
                                            if (patientId != null) {
                                                putInt("PATIENT_ID", patientId)
                                            }
                                            putBoolean("IS_DOCTOR", isDoctor)
                                            apply()
                                        }

                                        runOnUiThread {
                                            if (isDoctor) {
                                                startActivity(Intent(this@LoginActivity, DoctorLandingActivity::class.java))
                                                finish()
                                            } else {
                                                startActivity(Intent(this@LoginActivity, PatientLandingActivity::class.java))
                                                finish()
                                            }
                                        }
                                    } else {
                                        Log.d("LoginActivityLog", "User not found for Keycloak ID: $keycloakUserId")
                                        runOnUiThread {
                                            Toast.makeText(
                                                this@LoginActivity,
                                                "User not found.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                } else {
                                    Log.d("LoginActivityLog", "Error fetching user: ${response.message()}")
                                    runOnUiThread {
                                        Toast.makeText(
                                            this@LoginActivity,
                                            "An error occurred. Please try again.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }

                            override fun onFailure(call: retrofit2.Call<UserResponse>, t: Throwable) {
                                Log.d("LoginActivityLog", "Error on user fetch request: ${t.message}")
                                runOnUiThread {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Failed to fetch user details. Please try again.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        })
                    } else {
                        Log.d("LoginActivityLog", "Login failed with response: ${resp.message}")
                        runOnUiThread {
                            Toast.makeText(
                                this@LoginActivity,
                                "Login failed. Please check your credentials.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        })
    }

    private fun getKeycloakUserIdFromToken(accessToken: String): String? {
        return try {
            val jwt = JWT(accessToken)
            jwt.getClaim("sub").asString()
        } catch (e: Exception) {
            Log.d("LoginActivityLog", "Error parsing token: ${e.message}")
            null
        }
    }
}
