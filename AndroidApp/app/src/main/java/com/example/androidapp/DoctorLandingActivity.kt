package com.example.androidapp

import android.content.Intent
import android.content.res.Resources
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidapp.api.RetrofitClient
import com.example.androidapp.models.AppointmentResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class DoctorLandingActivity : AppCompatActivity() {

    private var doctorId: Int = -1
    private var refreshToken: String? = null
    private lateinit var appointmentList: LinearLayout
    private lateinit var datePicker: DatePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_landing)

        val sharedPreferences = getSharedPreferences("authPrefs", MODE_PRIVATE)
        refreshToken = sharedPreferences.getString("REFRESH_TOKEN", null)
        doctorId = sharedPreferences.getInt("PATIENT_ID", -1)
        val userFirstName: String? = sharedPreferences.getString("FIRST_NAME", "")
        val userLastName: String? = sharedPreferences.getString("LAST_NAME", "")

        if (doctorId == -1) {
            Toast.makeText(this, "Doctor ID not found.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val userNameTextView = findViewById<TextView>(R.id.userNameTextView2)
        userNameTextView.text = "$userFirstName $userLastName"

        val btnLogout = findViewById<ImageButton>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            performLogout(refreshToken)
        }

        appointmentList = findViewById(R.id.appointment_list)
        datePicker = findViewById(R.id.datePicker)
        datePicker.firstDayOfWeek = Calendar.MONDAY


        val today = Calendar.getInstance()
        datePicker.init(
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ) { _, year, month, dayOfMonth ->
            val selectedDate = String.format(Locale.getDefault(), "%02d-%02d-%04d", dayOfMonth, month + 1, year)
            fetchAppointments(selectedDate)
        }

        val todayDate = String.format(
            Locale("ro", "RO"),
            "%02d-%02d-%04d",
            today.get(Calendar.DAY_OF_MONTH),
            today.get(Calendar.MONTH) + 1,
            today.get(Calendar.YEAR)
        )
        fetchAppointments(todayDate)

        // Hide weekend days
        val dayPickerView = datePicker.findViewById<ViewGroup>(
            Resources.getSystem().getIdentifier("day", "id", "android")
        )
        if (dayPickerView != null) {
            for (i in 0 until dayPickerView.childCount) {
                val dayView = dayPickerView.getChildAt(i)
                val dayOfWeek = (i + datePicker.firstDayOfWeek) % 7
                if (dayOfWeek.equals(Calendar.SATURDAY) || dayOfWeek.equals(Calendar.SUNDAY)) {
                    dayView.visibility = View.GONE
                }
            }
        }

        datePicker.minDate = today.timeInMillis
    }

    private fun fetchAppointments(date: String) {
        if (doctorId == -1) {
            Toast.makeText(this, "Doctor ID invalid.", Toast.LENGTH_LONG).show()
            return
        }

        val noAppointmentsTextView = findViewById<TextView>(R.id.txtNoAppointments)
        appointmentList.removeAllViews()

        RetrofitClient.appointmentService.getAppointmentsByDoctorId(doctorId, date)
            .enqueue(object : Callback<List<AppointmentResponse>> {
                override fun onResponse(
                    call: Call<List<AppointmentResponse>>,
                    response: Response<List<AppointmentResponse>>
                ) {
                    runOnUiThread {
                        if (response.isSuccessful) {
                            val appointments = response.body()?.sortedWith(compareBy({ it.date }, { it.time })) ?: emptyList()

                            if (appointments.isEmpty()) {
                                noAppointmentsTextView.visibility = TextView.VISIBLE
                                appointmentList.visibility = TextView.GONE
                            } else {
                                noAppointmentsTextView.visibility = TextView.GONE
                                appointmentList.visibility = TextView.VISIBLE

                                for (appointment in appointments) {
                                    val appointmentContainer =
                                        LinearLayout(this@DoctorLandingActivity).apply {
                                            orientation = LinearLayout.VERTICAL
                                            setPadding(16, 16, 16, 16)
                                            setBackgroundColor(getColor(android.R.color.white))
                                            layoutParams = LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                            ).apply {
                                                setMargins(0, 8, 0, 8)
                                            }
                                        }

                                    val dateTextView = TextView(this@DoctorLandingActivity).apply {
                                        text = "Data: ${appointment.date}"
                                        textSize = 16f
                                        setTypeface(null, Typeface.BOLD)
                                        setTextColor(getColor(android.R.color.black))
                                    }
                                    appointmentContainer.addView(dateTextView)

                                    val timeTextView = TextView(this@DoctorLandingActivity).apply {
                                        text = "Ora: ${appointment.time}"
                                        textSize = 14f
                                        setTextColor(getColor(android.R.color.darker_gray))
                                    }
                                    appointmentContainer.addView(timeTextView)

                                    appointmentList.addView(appointmentContainer)
                                }
                            }
                        } else if (response.code() == 404) {
                            noAppointmentsTextView.visibility = TextView.VISIBLE
                            appointmentList.visibility = TextView.GONE
                        } else {
                            Toast.makeText(
                                this@DoctorLandingActivity,
                                "Eroare la preluarea programărilor: ${response.message()}",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.e(
                                "Appointments", "Error: ${response.code()} - ${response.message()}"
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<List<AppointmentResponse>>, t: Throwable) {
                    runOnUiThread {
                        Toast.makeText(
                            this@DoctorLandingActivity,
                            "Eroare de rețea: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("Appointments", "Failure: ${t.message}", t)
                    }
                }
            })
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

        val formBody = okhttp3.FormBody.Builder()
            .add("client_id", "android-app")
            .add("client_secret", "pHWo9QZW3f8avDCYSN5OSSoMcWCKNeCk")
            .add("refresh_token", refreshToken)
            .build()

        val request = okhttp3.Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        val client = okhttp3.OkHttpClient()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@DoctorLandingActivity, "Logout failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    if (it.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(this@DoctorLandingActivity, "Logout successful", Toast.LENGTH_LONG).show()
                            val sharedPrefs = getSharedPreferences("authPrefs", MODE_PRIVATE)
                            sharedPrefs.edit().clear().apply()

                            startActivity(Intent(this@DoctorLandingActivity, LoginActivity::class.java))
                            finish()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@DoctorLandingActivity, "Logout failed: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        })
    }
}