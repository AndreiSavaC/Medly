package com.example.androidapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidapp.api.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfirmAppointmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_appointment)

        // Primește ziua și ora trimise din MainActivity
        val selectedDate = intent.getStringExtra("selectedDate") ?: ""
        val selectedHour = intent.getStringExtra("selectedHour") ?: ""

        // Găsește TextView-urile și afișează datele primite
        val dateTextView = findViewById<TextView>(R.id.txtSelectedDate)
        val hourTextView = findViewById<TextView>(R.id.txtSelectedHour)
        val doctorTextView = findViewById<TextView>(R.id.txtDoctorName)
        val scheduleButton = findViewById<Button>(R.id.btnSchedule)

        // Numele doctorului hardcodat
        val doctorName = "Dr. Popescu Ion"

        dateTextView.text = "Zi selectată: $selectedDate"
        hourTextView.text = "Ora selectată: $selectedHour"
        doctorTextView.text = "Doctor: $doctorName"

        // Eveniment la apăsarea butonului Programează
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
                        finish() // Închide activitatea curentă
                    } else {
                        Toast.makeText(this@ConfirmAppointmentActivity, "Eroare: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@ConfirmAppointmentActivity, "Eroare de rețea: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
