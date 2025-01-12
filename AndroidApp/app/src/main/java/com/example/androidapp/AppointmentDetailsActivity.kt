// app/src/main/java/com/example/androidapp/AppointmentDetailsActivity.kt
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
    private lateinit var textViewTime: TextView
    private lateinit var textViewSymptoms: TextView
    private lateinit var textViewEmail: TextView
    private lateinit var textViewGender: TextView
    private lateinit var textViewHeight: TextView
    private lateinit var textViewWeight: TextView
    private lateinit var textViewBirthday: TextView

    private lateinit var buttonGenerateReport: Button
    private lateinit var textViewReport: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_details)

        // Inițializarea elementelor UI
        textViewPatientName = findViewById(R.id.textViewPatientName)
        textViewDate = findViewById(R.id.textViewDate)
        textViewTime = findViewById(R.id.textViewTime)
        textViewSymptoms = findViewById(R.id.textViewSymptoms)
        textViewEmail = findViewById(R.id.textViewEmail)
        textViewGender = findViewById(R.id.textViewGender)
        textViewHeight = findViewById(R.id.textViewHeight)
        textViewWeight = findViewById(R.id.textViewWeight)
        textViewBirthday = findViewById(R.id.textViewBirthday)

        buttonGenerateReport = findViewById(R.id.buttonGenerateReport)
        textViewReport = findViewById(R.id.textViewReport)

        // Obținerea datelor din Intent
        val patientName = intent.getStringExtra("patientName")
        val date = intent.getStringExtra("date")
        val time = intent.getStringExtra("time")
        val symptoms = intent.getStringExtra("symptoms")
        val email = intent.getStringExtra("email")
        val gender = intent.getStringExtra("gender")
        val height = intent.getFloatExtra("height", 0.0f)
        val weight = intent.getFloatExtra("weight", 0.0f)
        val birthday = intent.getStringExtra("birthday")

        // Afișarea datelor în TextView-uri
        textViewPatientName.text = "Patient: $patientName"
        textViewDate.text = "Data: $date"
        textViewTime.text = "Ora: $time"
        textViewSymptoms.text = "Simptome:\n• ${symptoms?.replace(", ", "\n• ") ?: "N/A"}"
        textViewEmail.text = "Email: $email"
        textViewGender.text = "Gen: $gender"
        textViewHeight.text = "Înălțime: $height cm"
        textViewWeight.text = "Greutate: $weight kg"
        textViewBirthday.text = "Data nașterii: $birthday"

        // Setarea listener-ului pentru butonul de generare raport
        buttonGenerateReport.setOnClickListener {
            // Extrage simptomele din TextView
            val symptomsText = symptoms ?: ""
            val symptomsList = symptomsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }

            if (symptomsList.isNotEmpty()) {
                // Lansează ReportActivity și trimite simptomele
                val intent = Intent(this, ReportActivity::class.java).apply {
                    putStringArrayListExtra("symptoms", ArrayList(symptomsList))
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Nu există simptome de trimis.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
