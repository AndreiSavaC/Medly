package com.example.androidapp

import android.os.Bundle
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class DoctorLandingActivity : AppCompatActivity() {

    private lateinit var appointmentAdapter: AppointmentAdapter
    private val appointmentsList = mutableListOf<Appointment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_landing)

        val recyclerViewAppointments: RecyclerView = findViewById(R.id.recyclerViewAppointments)
        appointmentAdapter = AppointmentAdapter(appointmentsList)
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
            appointmentsList.add(Appointment("John Doe", "10:00 AM", "Flu"))
            appointmentsList.add(Appointment("Jane Smith", "12:00 PM", "Cough"))
        } else {
            appointmentsList.add(Appointment("Alex Brown", "2:00 PM", "Headache"))
        }

        appointmentAdapter.notifyDataSetChanged()

        if (appointmentsList.isEmpty()) {
            Toast.makeText(this, "No appointments for $date", Toast.LENGTH_SHORT).show()
        }
    }
}
