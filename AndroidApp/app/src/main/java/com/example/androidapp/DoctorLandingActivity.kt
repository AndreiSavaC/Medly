package com.example.androidapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapp.api.RetrofitClient
import com.example.androidapp.models.Appointment
import com.example.androidapp.models.AppointmentResponse
import com.example.androidapp.models.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class DoctorLandingActivity : AppCompatActivity() {

    private lateinit var appointmentAdapter: AppointmentAdapter
    private val appointmentsList = mutableListOf<Appointment>()

    private var doctorId: Int = -1
    private var refreshToken: String? = null

    private lateinit var txtAvailableAppointments: TextView
    private lateinit var txtNoAppointments: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_landing)

        val btnLogout = findViewById<ImageButton>(R.id.btnLogout)
        val recyclerViewAppointments: RecyclerView = findViewById(R.id.recyclerViewAppointments)
        val datePicker: DatePicker = findViewById(R.id.datePicker)
        txtAvailableAppointments = findViewById(R.id.txtAvailableAppointments)
        txtNoAppointments = findViewById(R.id.txtNoAppointments)

        val today = Calendar.getInstance()
        datePicker.minDate = today.timeInMillis - 1000

        val sharedPreferences = getSharedPreferences("authPrefs", MODE_PRIVATE)
        refreshToken = sharedPreferences.getString("REFRESH_TOKEN", null)
        doctorId = sharedPreferences.getInt("PATIENT_ID", -1)

        if (doctorId == -1) {
            Toast.makeText(this, "Doctor ID nu a fost găsit.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        btnLogout.setOnClickListener {
            performLogout(refreshToken)
        }

        appointmentAdapter = AppointmentAdapter(appointmentsList) { appointment ->
            val intent = Intent(this, AppointmentDetailsActivity::class.java).apply {
                putExtra("patientName", appointment.patientName)
                putExtra("date", appointment.date)
                putExtra("time", appointment.time)
                putExtra("symptoms", appointment.symptoms)
                putExtra("email", appointment.email)
                putExtra("gender", appointment.gender)
                putExtra("height", appointment.height)
                putExtra("weight", appointment.weight)
                putExtra("birthday", appointment.birthday)
            }
            startActivity(intent)
        }
        recyclerViewAppointments.layoutManager = LinearLayoutManager(this)
        recyclerViewAppointments.adapter = appointmentAdapter

        datePicker.init(
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ) { _, year, month, dayOfMonth ->
            val selectedDate = String.format(Locale.getDefault(), "%02d-%02d-%04d", dayOfMonth, month + 1, year)
            txtAvailableAppointments.text = selectedDate
            loadAppointmentsForDate(selectedDate)
        }

        val todayDate = String.format(
            Locale.getDefault(),
            "%02d-%02d-%04d",
            today.get(Calendar.DAY_OF_MONTH),
            today.get(Calendar.MONTH) + 1,
            today.get(Calendar.YEAR)
        )
        txtAvailableAppointments.text = todayDate
        loadAppointmentsForDate(todayDate)
    }

    private fun loadAppointmentsForDate(date: String) {
        appointmentsList.clear()
        appointmentAdapter.notifyDataSetChanged()

        txtNoAppointments.visibility = View.GONE
        findViewById<RecyclerView>(R.id.recyclerViewAppointments).visibility = View.VISIBLE

        RetrofitClient.appointmentService.getAppointmentsByDoctorId(doctorId, date)
            .enqueue(object : Callback<List<AppointmentResponse>> {
                override fun onResponse(
                    call: Call<List<AppointmentResponse>>,
                    response: Response<List<AppointmentResponse>>
                ) {
                    if (response.isSuccessful) {
                        val appointmentResponses = response.body() ?: emptyList()
                        if (appointmentResponses.isEmpty()) {
                            runOnUiThread {
                                txtNoAppointments.text = "Nu există programări pentru $date."
                                txtNoAppointments.visibility = View.VISIBLE
                                findViewById<RecyclerView>(R.id.recyclerViewAppointments).visibility = View.GONE
                            }
                        } else {
                            fetchPatientDetails(appointmentResponses)
                        }
                    } else if (response.code() == 404) {
                        runOnUiThread {
                            txtNoAppointments.text = "Nu există programări pentru $date."
                            txtNoAppointments.visibility = View.VISIBLE
                            findViewById<RecyclerView>(R.id.recyclerViewAppointments).visibility = View.GONE
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@DoctorLandingActivity,
                                "Eroare la încărcarea programărilor: ${response.message()}",
                                Toast.LENGTH_LONG
                            ).show()
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
                    }
                }
            })
    }

    private fun fetchPatientDetails(appointmentResponses: List<AppointmentResponse>) {
        val totalAppointments = appointmentResponses.size
        var processedAppointments = 0

        fun checkCompletion() {
            processedAppointments++
            if (processedAppointments == totalAppointments) {
                runOnUiThread {
                    appointmentAdapter.notifyDataSetChanged()
                }
            }
        }

        for (appointmentResponse in appointmentResponses) {
            val patientId = appointmentResponse.patientId

            RetrofitClient.userService.getUserById(patientId)
                .enqueue(object : Callback<UserResponse> {
                    override fun onResponse(
                        call: Call<UserResponse>,
                        response: Response<UserResponse>
                    ) {
                        if (response.isSuccessful) {
                            val userResponse = response.body()
                            if (userResponse != null) {
                                val name = "${userResponse.firstName} ${userResponse.lastName}"
                                val appointment = mapToAppointment(userResponse, appointmentResponse)
                                appointmentsList.add(appointment)
                            } else {
                                val appointment = mapToAppointment(null, appointmentResponse)
                                appointmentsList.add(appointment)
                            }
                        } else {
                            val appointment = mapToAppointment(null, appointmentResponse)
                            appointmentsList.add(appointment)
                        }
                        checkCompletion()
                    }

                    override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                        val appointment = mapToAppointment(null, appointmentResponse)
                        appointmentsList.add(appointment)
                        checkCompletion()
                    }
                })
        }
    }

    private fun mapToAppointment(userResponse: UserResponse?, appointmentResponse: AppointmentResponse): Appointment {
        return Appointment(
            patientName = userResponse?.let { "${it.firstName} ${it.lastName}" } ?: "Necunoscut",
            date = appointmentResponse.date,
            time = appointmentResponse.time,
            symptoms = appointmentResponse.symptoms.joinToString(", "),
            email = userResponse?.email ?: "N/A",
            gender = userResponse?.gender ?: "N/A",
            height = userResponse?.height ?: 0.0f,
            weight = userResponse?.weight ?: 0.0f,
            birthday = userResponse?.birthday ?: "N/A"
        )
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
//{
//    "id": 2,
//    "keycloakId": "489f2c3d-a773-4d4a-8657-55a59cdd6362",
//    "firstName": "Andrei",
//    "lastName": "Maciuca",
//    "email": "andreimaciuca@maciuca.com",
//    "gender": "Male",
//    "height": 190.0,
//    "weight": 110.0,
//    "birthday": "2000-08-04",
//    "doctorId": 1,
//    "isDoctor": false,
//    "isAdmin": false
//}