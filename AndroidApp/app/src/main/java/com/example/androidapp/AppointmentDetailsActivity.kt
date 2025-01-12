package com.example.androidapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class AppointmentDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_details)

        val patientName = intent.getStringExtra("patientName")
        val date = intent.getStringExtra("date")
        val time = intent.getStringExtra("time")
        val symptoms = intent.getStringExtra("symptoms")

        findViewById<TextView>(R.id.textViewPatientName).text = "Patient: $patientName"
        findViewById<TextView>(R.id.textViewDate).text = "Date: $date"
        findViewById<TextView>(R.id.textViewTime).text = "Time: $time"
        findViewById<TextView>(R.id.textViewSymptoms).text = "Symptoms: $symptoms"
    }
}
