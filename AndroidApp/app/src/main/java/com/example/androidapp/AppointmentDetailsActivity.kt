package com.example.androidapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AppointmentDetailsActivity : AppCompatActivity() {

    private lateinit var textViewPatientName: TextView
    private lateinit var textViewDate: TextView
    private lateinit var textViewSymptoms: TextView
    private lateinit var textViewHeight: TextView
    private lateinit var textViewWeight: TextView
    private lateinit var textViewBirthday: TextView

    private lateinit var buttonGenerateReport: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_details)

        textViewPatientName = findViewById(R.id.txtPacienteName)
        textViewDate = findViewById(R.id.txtSelectedDate)
        textViewSymptoms = findViewById(R.id.txtSymptoms)
        textViewHeight = findViewById(R.id.txtHeight)
        textViewWeight = findViewById(R.id.txtWeight)
        textViewBirthday = findViewById(R.id.txtBirthdate)

        buttonGenerateReport = findViewById(R.id.btnGenerateReport)

        val patientName = intent.getStringExtra("patientName")
        val date = intent.getStringExtra("date")
        val time = intent.getStringExtra("time")
        val symptoms = intent.getStringExtra("symptoms")
        val height = intent.getFloatExtra("height", 0.0f)
        val weight = intent.getFloatExtra("weight", 0.0f)
        val birthday = intent.getStringExtra("birthday")

        textViewPatientName.text = "Pacient: $patientName"
        textViewDate.text = "Data: $date la ora $time"
        textViewSymptoms.text = "Simptome\n• ${symptoms?.replace(", ", "\n• ") ?: "N/A"}"
        textViewHeight.text = "Înălțime: $height cm"
        textViewWeight.text = "Greutate: $weight kg"
        textViewBirthday.text = "Data nașterii: $birthday"

        buttonGenerateReport.setOnClickListener {
            val symptomsText = symptoms ?: ""
            val symptomsList = symptomsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }

            if (symptomsList.isNotEmpty()) {
                val intent = Intent(this, ReportActivity::class.java).apply {
                    putStringArrayListExtra("symptoms", ArrayList(symptomsList))
                    putExtra("patientName", patientName)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Nu există simptome de trimis.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}