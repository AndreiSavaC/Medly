package com.example.androidapp

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.view.Gravity
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.androidapp.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var weekStart: Calendar
    private lateinit var weekRangeTextView: TextView
    private lateinit var weekCalendar: LinearLayout
    private lateinit var hourListContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inițializarea componentelor UI
        weekRangeTextView = findViewById(R.id.txtWeekRange)
        weekCalendar = findViewById(R.id.weekCalendar)
        hourListContainer = findViewById(R.id.hourListContainer)

        val btnPrevWeek = findViewById<TextView>(R.id.btnPrevWeek)
        val btnNextWeek = findViewById<TextView>(R.id.btnNextWeek)

        // Setăm săptămâna curentă
        weekStart = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }

        // Actualizăm UI-ul pentru săptămâna curentă
        updateWeekDisplay()

        // Navigare săptămâni
        btnPrevWeek.setOnClickListener {
            weekStart.add(Calendar.WEEK_OF_YEAR, -1)
            updateWeekDisplay()
        }

        btnNextWeek.setOnClickListener {
            weekStart.add(Calendar.WEEK_OF_YEAR, 1)
            updateWeekDisplay()
        }

        // Fetch appointments pentru ziua selectată (Inițial pentru prima zi din săptămână)
        fetchAppointmentsForSelectedDate(getFormattedDate(weekStart))

        // Selectarea unei zile și fetch la orele disponibile
        weekCalendar.setOnClickListener {
            val selectedDay = getFormattedDate(weekStart)  // Obținem data selectată
            fetchAppointmentsForSelectedDate(selectedDay)  // Apelăm API-ul pentru ziua respectivă
        }
    }

    // Funcție pentru a face fetch la API pentru ziua selectată
    private fun fetchAppointmentsForSelectedDate(selectedDate: String) {
        val doctorId = 1  // ID-ul doctorului hardcodat

        RetrofitClient.appointmentService.getAvailableHours(doctorId, selectedDate).enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val availableHours = response.body() ?: emptyList()
                    Log.d("AvailableHours", "Available hours for $selectedDate: $availableHours")
                    updateHourList(availableHours)  // Actualizăm lista cu orele disponibile
                } else {
                    Toast.makeText(this@MainActivity, "Failed to fetch available hours", Toast.LENGTH_SHORT).show()
                    Log.e("AvailableHours", "Error fetching available hours: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("AvailableHours", "Error: ${t.message}", t)
            }
        })
    }

    // Actualizează lista cu orele disponibile
    private fun updateHourList(availableHours: List<String>) {
        hourListContainer.removeAllViews() // Ștergem orele existente

        // Parcurgem fiecare oră disponibilă și o adăugăm la UI
        for (hour in availableHours) {
            val hourView = TextView(this).apply {
                text = hour
                gravity = Gravity.START
                setPadding(16, 8, 16, 8)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            hourListContainer.addView(hourView)
        }
    }

    // Funcție care formatează data într-un format de tip string (ex: "26-11-2024")
    private fun getFormattedDate(calendar: Calendar): String {
        val day = calendar.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0') // Adăugăm 0 în față dacă ziua e mai mică de 10
        val month = (calendar.get(Calendar.MONTH) + 1).toString().padStart(2, '0') // Adăugăm 0 în față la lunile 1-9
        val year = calendar.get(Calendar.YEAR)

        return "$day-$month-$year" // Formatul DD-MM-YYYY
    }

    // Funcție pentru a actualiza afișarea săptămânii
    private fun updateWeekDisplay() {
        // Calculăm sfârșitul săptămânii
        val weekEnd = weekStart.clone() as Calendar
        weekEnd.add(Calendar.DAY_OF_MONTH, 6)

        // Actualizăm textul cu intervalul săptămânii
        weekRangeTextView.text = getFormattedWeekRange(weekStart, weekEnd)

        // Actualizăm zilele săptămânii
        updateWeekDays()
    }

    // Funcție personalizată pentru a obține intervalul săptămânii
    private fun getFormattedWeekRange(start: Calendar, end: Calendar): String {
        val startDay = start.get(Calendar.DAY_OF_MONTH)
        val startMonth = start.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
        val endDay = end.get(Calendar.DAY_OF_MONTH)
        val endMonth = end.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())

        return "$startDay $startMonth - $endDay $endMonth"
    }

    // Actualizează zilele săptămânii în layout
    private fun updateWeekDays() {
        weekCalendar.removeAllViews() // Ștergem zilele existente

        val daysOfWeek = listOf("Lun.", "Mar.", "Mie.", "Joi.", "Vin.", "Sâm.", "Dum.")
        val currentDay = Calendar.getInstance()

        for (i in 0..6) {
            // Calculăm fiecare zi din săptămână
            val day = weekStart.clone() as Calendar
            day.add(Calendar.DAY_OF_MONTH, i)

            // Creăm un TextView pentru fiecare zi
            val dayView = TextView(this).apply {
                text = "${daysOfWeek[i]} ${day.get(Calendar.DAY_OF_MONTH)}"
                gravity = Gravity.CENTER
                setPadding(16, 8, 16, 8)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                setBackgroundResource(if (isSameDay(currentDay, day)) R.drawable.day_selector else android.R.color.transparent)

                // Selectăm ziua la click
                setOnClickListener {
                    clearSelection()
                    setBackgroundResource(R.drawable.day_selector)

                    // După ce selectezi o zi, facem fetch la orele disponibile pentru acea zi
                    fetchAppointmentsForSelectedDate(getFormattedDate(day))
                }
            }
            weekCalendar.addView(dayView)
        }
    }

    // Verifică dacă două zile sunt aceleași
    private fun isSameDay(day1: Calendar, day2: Calendar): Boolean {
        return day1.get(Calendar.YEAR) == day2.get(Calendar.YEAR) &&
                day1.get(Calendar.DAY_OF_YEAR) == day2.get(Calendar.DAY_OF_YEAR)
    }

    // Curăță selecția zilelor
    private fun clearSelection() {
        for (i in 0 until weekCalendar.childCount) {
            val child = weekCalendar.getChildAt(i)
            child.setBackgroundResource(android.R.color.transparent)
        }
    }
}
