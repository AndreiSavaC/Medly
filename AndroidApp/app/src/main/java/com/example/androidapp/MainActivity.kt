package com.example.androidapp

import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.androidapp.utils.DateUtils
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var weekStart: Calendar
    private lateinit var weekRangeTextView: TextView
    private lateinit var weekCalendar: LinearLayout
    private var selectedDay: Calendar? = null // Variabilă pentru a stoca ziua selectată

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inițializarea componentelor UI
        weekRangeTextView = findViewById(R.id.txtWeekRange)
        weekCalendar = findViewById(R.id.weekCalendar)

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
    }

    // Actualizează afișarea săptămânii în funcție de interval
    private fun updateWeekDisplay() {
        // Calculăm sfârșitul săptămânii
        val weekEnd = weekStart.clone() as Calendar
        weekEnd.add(Calendar.DAY_OF_MONTH, 6)

        // Actualizăm textul cu intervalul săptămânii
        weekRangeTextView.text = DateUtils.getFormattedWeekRange(weekStart, weekEnd)

        // Actualizăm zilele săptămânii
        updateWeekDays()
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
                setBackgroundResource(
                    if (isSameDay(selectedDay, day)) R.drawable.day_selector else android.R.color.transparent
                )

                // Selectăm ziua la click
                setOnClickListener {
                    selectedDay = day // Salvează ziua selectată
                    updateWeekDays() // Reîmpinge zilele săptămânii cu selecția actualizată
                }
            }
            weekCalendar.addView(dayView)
        }
    }

    // Verifică dacă două zile sunt aceleași
    private fun isSameDay(day1: Calendar?, day2: Calendar): Boolean {
        return day1?.get(Calendar.YEAR) == day2.get(Calendar.YEAR) &&
                day1.get(Calendar.DAY_OF_YEAR) == day2.get(Calendar.DAY_OF_YEAR)
    }
}

