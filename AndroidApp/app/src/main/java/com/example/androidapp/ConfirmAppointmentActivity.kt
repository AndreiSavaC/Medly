package com.example.androidapp

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidapp.api.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar

class ConfirmAppointmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_appointment)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }

        val selectedDate = intent.getStringExtra("selectedDate") ?: ""
        val selectedHour = intent.getStringExtra("selectedHour") ?: ""
        val selectedSymptoms: ArrayList<String> = intent.getStringArrayListExtra("selectedSymptoms") ?: ArrayList()

        val dateTextView = findViewById<TextView>(R.id.txtSelectedDate)
        val hourTextView = findViewById<TextView>(R.id.txtSelectedHour)
        val doctorTextView = findViewById<TextView>(R.id.txtDoctorName)
        val symptomsCategoryContainer = findViewById<LinearLayout>(R.id.containerSymptomsCategory)
        val scheduleButton = findViewById<Button>(R.id.btnSchedule)

        val doctorName = "Dr. Popescu Ion"

        dateTextView.text = "Zi selectată: $selectedDate"
        hourTextView.text = "Ora selectată: $selectedHour"
        doctorTextView.text = "Doctor: $doctorName"

        // Grupa simptomele după categorie
        val symptomsByCategory = mutableMapOf<String, MutableList<String>>()
        for (item in selectedSymptoms) {
            val parts = item.split(":")
            if (parts.size == 2) {
                val category = parts[0].trim()
                val symptoms = parts[1].split(",").map { it.trim() }
                symptomsByCategory[category] = symptomsByCategory.getOrDefault(category, mutableListOf()).apply {
                    addAll(symptoms)
                }
            }
        }

        if (symptomsByCategory.isNotEmpty()) {
            for ((category, symptoms) in symptomsByCategory) {
                val categoryTextView = TextView(this).apply {
                    text = "$category:"
                    textSize = 18f
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    setPadding(0, 8, 0, 8)
                }
                symptomsCategoryContainer.addView(categoryTextView)

                symptoms.forEach { symptom ->
                    val symptomTextView = TextView(this).apply {
                        text = "- $symptom"
                        textSize = 16f
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

        scheduleButton.setOnClickListener {
            scheduleAppointment(selectedDate, selectedHour)
        }
    }

    private fun scheduleAppointment(date: String, time: String) {
        val requestBody = mapOf(
            "pacientId" to 1,
            "doctorId" to 1,
            "date" to date,
            "time" to time
        )

        RetrofitClient.appointmentService.createAppointment(requestBody)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ConfirmAppointmentActivity, "Programare realizată cu succes!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@ConfirmAppointmentActivity, "Eroare: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@ConfirmAppointmentActivity, "Eroare de rețea: ${t.message}", Toast.LENGTH_SHORT).show()
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
