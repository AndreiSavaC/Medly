package com.example.androidapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.androidapp.api.RetrofitClient
import com.example.androidapp.models.AppointmentRequest
import com.example.androidapp.models.UserResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfirmAppointmentActivity : AppCompatActivity() {

    private var patientId: Int = -1
    private var doctorId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_appointment)

        val sharedPrefs = getSharedPreferences("authPrefs", MODE_PRIVATE)
        patientId = sharedPrefs.getInt("PATIENT_ID", -1)
        doctorId = sharedPrefs.getInt("DOCTOR_ID", -1)

        if (patientId == -1 || doctorId == -1) {
            Toast.makeText(
                this, "Eroare: Informațiile utilizatorului sunt lipsă.", Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }

        val selectedDate = intent.getStringExtra("selectedDate") ?: ""
        val selectedHour = intent.getStringExtra("selectedHour") ?: ""
        val dateTextView = findViewById<TextView>(R.id.txtSelectedDate)
        val hourTextView = findViewById<TextView>(R.id.txtSelectedHour)
        val doctorTextView = findViewById<TextView>(R.id.txtDoctorName)
        val symptomsCategoryContainer = findViewById<LinearLayout>(R.id.containerSymptomsCategory)
        val scheduleButton = findViewById<Button>(R.id.btnSchedule)

        doctorTextView.text = "Doctor: Loading..."

        dateTextView.text = "Zi selectată: $selectedDate"
        hourTextView.text = "Ora selectată: $selectedHour"

        val symptomsList = intent.getStringArrayListExtra("selectedSymptoms") ?: arrayListOf()
        val categorySymptomsList =
            intent.getStringArrayListExtra("categorySymptoms") ?: arrayListOf()

        val symptomsByCategory = mutableMapOf<String, MutableList<String>>()

        for (categorySymptoms in categorySymptomsList) {
            val parts = categorySymptoms.split(":")
            if (parts.size == 2) {
                val categoryName = parts[0].trim()
                val symptomNames = parts[1].split(",").map { it.trim() }
                symptomsByCategory[categoryName] =
                    symptomsByCategory.getOrDefault(categoryName, mutableListOf()).apply {
                        addAll(symptomNames)
                    }
            }
        }

        if (symptomsByCategory.isNotEmpty()) {
            for ((category, symptoms) in symptomsByCategory) {
                val categoryTextView = TextView(this).apply {
                    text = category
                    textSize = 20f
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    setPadding(0, 8, 0, 8)
                }
                categoryTextView.setTextColor(resources.getColor(R.color.black))
                symptomsCategoryContainer.addView(categoryTextView)

                symptoms.forEach { symptom ->
                    val symptomTextView = TextView(this).apply {
                        text = "- $symptom"
                        textSize = 18f
                        setPadding(16, 4, 0, 4)
                    }
                    symptomsCategoryContainer.addView(symptomTextView)
                }
            }
        } else {
            val noSymptomsTextView = TextView(this).apply {
                text = "Nu au fost selectate simptome."
                textSize = 16f
            }
            symptomsCategoryContainer.addView(noSymptomsTextView)
        }

        fetchDoctorName(doctorId, doctorTextView)

        scheduleButton.setOnClickListener {
            scheduleAppointment(selectedDate, selectedHour, symptomsList)
        }
    }

    private fun fetchDoctorName(doctorId: Int, doctorTextView: TextView) {
        if (doctorId == -1) {
            doctorTextView.text = "Doctor: N/A"
            Toast.makeText(
                this, "Eroare: ID-ul doctorului este invalid.", Toast.LENGTH_LONG
            ).show()
            return
        }

        RetrofitClient.userService.getUserById(doctorId).enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>, response: Response<UserResponse>
            ) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        val fullName = "${user.firstName} ${user.lastName}"
                        doctorTextView.text = "Doctor: $fullName"
                    } else {
                        doctorTextView.text = "Doctor: N/A"
                        Toast.makeText(
                            this@ConfirmAppointmentActivity,
                            "Eroare: Nu s-a găsit doctorul.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    doctorTextView.text = "Doctor: N/A"
                    Toast.makeText(
                        this@ConfirmAppointmentActivity,
                        "Eroare la încărcarea datelor doctorului: ${response.message()}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e(
                        "ConfirmAppointment", "Eroare la API: ${response.errorBody()?.string()}"
                    )
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                doctorTextView.text = "Doctor: N/A"
                Toast.makeText(
                    this@ConfirmAppointmentActivity,
                    "Eroare de rețea: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("ConfirmAppointment", "Eroare de rețea", t)
            }
        })
    }

    private fun scheduleAppointment(date: String, time: String, symptoms: List<String>) {
        if (patientId == -1 || doctorId == -1) {
            Toast.makeText(
                this, "Eroare: Informațiile utilizatorului sunt lipsă.", Toast.LENGTH_LONG
            ).show()
            return
        }

        val appointmentRequest = AppointmentRequest(
            date = date,
            time = time,
            patientId = patientId,
            doctorId = doctorId,
            symptoms = symptoms
        )

        RetrofitClient.appointmentService.createAppointment(appointmentRequest)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>, response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@ConfirmAppointmentActivity,
                            "Programare realizată cu succes!",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(
                            this@ConfirmAppointmentActivity, PatientLandingActivity::class.java
                        )

                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                        startActivity(intent)

                        finish()
                    } else {
                        Toast.makeText(
                            this@ConfirmAppointmentActivity,
                            "Eroare: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(
                            "ConfirmAppointment",
                            "Eroare la programare: ${response.errorBody()?.string()}"
                        )
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(
                        this@ConfirmAppointmentActivity,
                        "Eroare de rețea: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("ConfirmAppointment", "Eroare de rețea", t)
                }
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
