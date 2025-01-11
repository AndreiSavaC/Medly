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
                if (response.isSuccessful) {
                    val responseBody = response.body?.string();
                    val json = JSONObject(responseBody);
                    val accessToken = json.getString("access_token");

                    val keycloakUserId = getKeycloakUserIdFromToken(accessToken);
                    Log.d(("LoginActivityLog"), "Keycloak user ID: $keycloakUserId");

                    if (keycloakUserId == null) {
                        Log.d("LoginActivityLog", "Invalid token or missing sub");
                        runOnUiThread {
                            Toast.makeText(this@LoginActivity, "Authentication failed. Please try again.", Toast.LENGTH_LONG).show();
                        };
                        return;
                    }

                    val callUser = RetrofitClient.userService.getUserByKeycloakId(keycloakUserId);
                    callUser.enqueue(object : retrofit2.Callback<UserResponse> {
                        override fun onResponse(call: retrofit2.Call<UserResponse>, response: retrofit2.Response<UserResponse>) {
                            if (response.isSuccessful) {
                                val userResponse = response.body();
                                Log.d("LoginActivityLog", "User : $userResponse");
                                if (userResponse != null) {
                                    val isDoctor = userResponse.isDoctor;
                                    runOnUiThread {
                                        if (isDoctor) {
                                            val intent = Intent(this@LoginActivity, DoctorLandingActivity::class.java);
                                            intent.putExtra("ACCESS_TOKEN", accessToken);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            val intent = Intent(this@LoginActivity, LandingActivity::class.java);
                                            intent.putExtra("ACCESS_TOKEN", accessToken);
                                            startActivity(intent);
                                            finish();
                                        }
                                    };
                                } else {
                                    Log.d("LoginActivityLog", "User not found for Keycloak ID: $keycloakUserId");
                                    runOnUiThread {
                                        Toast.makeText(this@LoginActivity, "User not found.", Toast.LENGTH_LONG).show();
                                    };
                                }
                            } else {
                                Log.d("LoginActivityLog", "Error fetching user: ${response.message()}");
                                runOnUiThread {
                                    Toast.makeText(this@LoginActivity, "1An error occurred. Please try again.", Toast.LENGTH_LONG).show();
                                };
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<UserResponse>, t: Throwable) {
                            Log.d("LoginActivityLog", "Error on user fetch request: ${t.message}");
                            runOnUiThread {
                                Toast.makeText(this@LoginActivity, "Failed to fetch user details. Please try again.", Toast.LENGTH_LONG).show();
                            };
                        }
                    });
                } else {
                    Log.d("LoginActivityLog", "Login failed with response: ${response.message}");
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Login failed. Please check your credentials.", Toast.LENGTH_LONG).show();
                    };
                }
            }
        });
    }

    private fun getKeycloakUserIdFromToken(accessToken: String): String? {
        return try {
            val jwt = JWT(accessToken);
            jwt.getClaim("sub").asString();
        } catch (e: Exception) {
            Log.d("LoginActivityLog", "Error parsing token: ${e.message}");
            null;
        };
    }
}