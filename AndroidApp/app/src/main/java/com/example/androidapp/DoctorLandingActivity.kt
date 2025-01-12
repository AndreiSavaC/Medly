package com.example.androidapp

import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.*

class DoctorLandingActivity : AppCompatActivity() {

    private lateinit var appointmentAdapter: AppointmentAdapter
    private val appointmentsList = mutableListOf<Appointment>()

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_landing)

        val sharedPreferences = getSharedPreferences("authPrefs", MODE_PRIVATE)
        val refreshToken = sharedPreferences.getString("REFRESH_TOKEN", null)

        val btnLogout = findViewById<ImageButton>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            performLogout(refreshToken)
        }

        val recyclerViewAppointments: RecyclerView = findViewById(R.id.recyclerViewAppointments)
        appointmentAdapter = AppointmentAdapter(appointmentsList) { appointment ->
            val intent = Intent(this, AppointmentDetailsActivity::class.java).apply {
                putExtra("patientName", appointment.patientName)
                putExtra("date", appointment.date)
                putExtra("time", appointment.time)
                putExtra("symptoms", appointment.symptoms)
            }
            startActivity(intent)
        }s
        recyclerViewAppointments.layoutManager = LinearLayoutManager(this)
        recyclerViewAppointments.adapter = appointmentAdapter

        val datePicker: DatePicker = findViewById(R.id.datePicker)
        val txtAvailableAppointments: TextView = findViewById(R.id.txtAvailableAppointments)

        datePicker.init(
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        ) { _, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth-${month + 1}-$year"

            txtAvailableAppointments.text = selectedDate

            loadAppointmentsForDate(selectedDate)
        }

        val today = Calendar.getInstance()
        val todayDate = "${today.get(Calendar.DAY_OF_MONTH)}-${today.get(Calendar.MONTH) + 1}-${today.get(Calendar.YEAR)}"
        txtAvailableAppointments.text = todayDate
        loadAppointmentsForDate(todayDate)
    }

    private fun loadAppointmentsForDate(date: String) {
        appointmentsList.clear()

        if (date == "4-1-2025") {
            appointmentsList.add(Appointment("John Doe", "10:00 AM", "4-1-2025","awdwa"))
            appointmentsList.add(Appointment("Jane Smith", "12:00 PM","4-1-2025","awdwa" ))
        } else {
            appointmentsList.add(Appointment("Alex Brown", "2:00 PM","4-1-2025","awdwa" ))
        }

        appointmentAdapter.notifyDataSetChanged()

        if (appointmentsList.isEmpty()) {
            Toast.makeText(this, "No appointments for $date", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this@DoctorLandingActivity, "Logout failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
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
