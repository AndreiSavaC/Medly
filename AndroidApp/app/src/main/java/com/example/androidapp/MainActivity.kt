package com.example.androidapp

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.view.Gravity
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.androidapp.api.RetrofitClient
import com.example.androidapp.models.Appointment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var weekStart: Calendar
    private lateinit var weekRangeTextView: TextView
    private lateinit var weekCalendar: LinearLayout
    private lateinit var hourListContainer: LinearLayout
    private lateinit var appointments: List<Appointment>

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

        // Fetch appointments
        fetchAppointments()
    }

    // Fetch appointments din API


    private fun fetchAppointments() {
        // Fă cererea HTTP pentru a obține programările
        RetrofitClient.appointmentService.getAppointments().enqueue(object : Callback<List<Appointment>> {
            override fun onResponse(call: Call<List<Appointment>>, response: Response<List<Appointment>>) {
                if (response.isSuccessful) {
                    appointments = response.body() ?: emptyList()
                    Log.d("FetchAppointments", "Appointments fetched successfully: $appointments")
                    updateHourList()  // Actualizăm lista cu ore
                } else {
                    Toast.makeText(this@MainActivity, "Failed to fetch appointments", Toast.LENGTH_SHORT).show()
                    Log.e("FetchAppointments", "Error fetching appointments: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Appointment>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("FetchAppointments", "Error: ${t.message}", t)
            }
        })
    }


    // Actualizează lista cu orele disponibile
    private fun updateHourList() {
        hourListContainer.removeAllViews() // Ștergem orele existente

        val startHour = 8
        val endHour = 18

        // Obținem ziua selectată
        val selectedDate = getFormattedDate(weekStart)
        Log.d("SelectedDate", "Selected date: $selectedDate")  // Log pentru data selectată

        // Parcurgem fiecare oră din intervalul 8:00 - 18:00
        for (hour in startHour..endHour) {
            // Verificăm fiecare minut 0 și 30 pentru a adăuga sau ascunde ora
            for (minute in arrayOf(0, 30)) {
                val hourText = String.format("%02d:%02d", hour, minute)

                // Log pentru fiecare ora verificată
                Log.d("HourCheck", "Checking hour: $hourText")

                // Verificăm dacă există o programare pentru această oră în ziua respectivă
                val appointmentExists = appointments.any {
                    Log.d("AppointmentCheck", "Comparing appointment: ${it.date} with selected date: $selectedDate and time: ${it.time} with hour: $hourText")
                    it.date == selectedDate && it.time == hourText
                }

                if (appointmentExists) {
                    Log.d("AppointmentExists", "Appointment exists for $hourText on $selectedDate. Skipping.")
                    // Dacă există o programare la acea oră, o ignorăm (nu o adăugăm la UI)
                    continue
                }

                // Creăm un TextView pentru fiecare oră disponibilă
                val hourView = TextView(this).apply {
                    text = hourText
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
    }

    // Funcție care formatează data într-un format de tip string (ex: "26-11-2024")
    private fun getFormattedDate(calendar: Calendar): String {
        val day = calendar.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0') // Adăugăm 0 în față dacă ziua e mai mică de 10
        val month = (calendar.get(Calendar.MONTH) + 1).toString().padStart(2, '0') // Adăugăm 0 în față la lunile 1-9
        val year = calendar.get(Calendar.YEAR)

        val formattedDate = "$day-$month-$year" // Formatul DD-MM-YYYY
        Log.d("FormattedDate", "Formatted date: $formattedDate")  // Log pentru data formatată
        return formattedDate
    }

    // Actualizează afișarea săptămânii în funcție de interval
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
