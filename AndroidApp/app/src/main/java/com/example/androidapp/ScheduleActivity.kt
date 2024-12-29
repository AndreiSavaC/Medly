package com.example.androidapp

import android.content.Intent
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
import androidx.core.content.ContextCompat


class ScheduleActivity : AppCompatActivity() {

    private lateinit var weekStart: Calendar
    private lateinit var weekRangeTextView: TextView
    private lateinit var weekCalendar: LinearLayout
    private lateinit var hourListContainer: LinearLayout
    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.schedule_activity)

        weekRangeTextView = findViewById(R.id.txtWeekRange)
        weekCalendar = findViewById(R.id.weekCalendar)
        hourListContainer = findViewById(R.id.hourListContainer)

        val btnPrevWeek = findViewById<TextView>(R.id.btnPrevWeek)
        val btnNextWeek = findViewById<TextView>(R.id.btnNextWeek)

        weekStart = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }

        updateWeekDisplay()

        selectCurrentDayAndFetchAppointments()

        btnPrevWeek.setOnClickListener {
            weekStart.add(Calendar.WEEK_OF_YEAR, -1)
            updateWeekDisplay()
        }

        btnNextWeek.setOnClickListener {
            weekStart.add(Calendar.WEEK_OF_YEAR, 1)
            updateWeekDisplay()
        }
    }

    private fun fetchAppointmentsForSelectedDate(selectedDate: String) {
        val doctorId = 1

        RetrofitClient.appointmentService.getAvailableHours(doctorId, selectedDate).enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val availableHours = response.body() ?: emptyList()
                    Log.d("AvailableHours", "Available hours for $selectedDate: $availableHours")
                    updateHourList(availableHours)
                } else {
                    Toast.makeText(this@ScheduleActivity, "Failed to fetch available hours", Toast.LENGTH_SHORT).show()
                    Log.e("AvailableHours", "Error fetching available hours: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Toast.makeText(this@ScheduleActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("AvailableHours", "Error: ${t.message}", t)
            }
        })
    }

    private fun updateHourList(availableHours: List<String>) {
        hourListContainer.removeAllViews()

        val isToday = isSameDay(selectedDate, Calendar.getInstance())

        for (hour in availableHours) {
            if (isToday && !isAfterCurrentTime(hour)) {
                continue
            }

            val hourView = layoutInflater.inflate(R.layout.hour_item, hourListContainer, false)

            val hourTextView = hourView.findViewById<TextView>(R.id.tvHour)
            hourTextView.text = hour

            hourView.setOnClickListener {
                val selectedDateFormatted = getFormattedDate(selectedDate)

                val intent = Intent(this@ScheduleActivity, SymptomsActivity::class.java).apply {
                    putExtra("selectedDate", selectedDateFormatted)
                    putExtra("selectedHour", hour)
                }
                startActivity(intent)
            }
            hourListContainer.addView(hourView)
        }
    }

    private fun getFormattedDate(calendar: Calendar): String {
        val day = calendar.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
        val month = (calendar.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
        val year = calendar.get(Calendar.YEAR)

        return "$day-$month-$year"
    }

    private fun updateWeekDisplay() {
        val weekEnd = weekStart.clone() as Calendar
        weekEnd.add(Calendar.DAY_OF_MONTH, 6)

        weekRangeTextView.text = getFormattedWeekRange(weekStart, weekEnd)
        updateWeekDays()
    }

    private fun getFormattedWeekRange(start: Calendar, end: Calendar): String {
        val startDay = start.get(Calendar.DAY_OF_MONTH)
        val startMonth = start.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale("ro", "RO"))
        val endDay = end.get(Calendar.DAY_OF_MONTH)
        val endMonth = end.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale("ro", "RO"))
        return "$startDay $startMonth - $endDay $endMonth"
    }

    private fun updateWeekDays() {
        weekCalendar.removeAllViews()
        val daysOfWeek = listOf("Lun.", "Mar.", "Mie.", "Joi.", "Vin.", "Sâm.", "Dum.")

        for (i in 0..6) {
            val day = weekStart.clone() as Calendar
            day.add(Calendar.DAY_OF_MONTH, i)

            val isPastDay = day.before(Calendar.getInstance()) && !isSameDay(day, Calendar.getInstance())

            val dayView = TextView(this).apply {
                text = "${daysOfWeek[i]}\n${day.get(Calendar.DAY_OF_MONTH)}"
                gravity = Gravity.CENTER
                setPadding(16, 8, 16, 8)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

                setTextColor(ContextCompat.getColor(this@ScheduleActivity, android.R.color.black))

                if (isPastDay) {
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, android.R.color.darker_gray))
                    isClickable = false
                } else {
                    setTextColor(ContextCompat.getColor(this@ScheduleActivity, android.R.color.black))
                    isClickable = true
                }

                setOnClickListener {
                    if (!isPastDay) {
                        clearSelection()
                        setBackgroundResource(R.drawable.day_selector)
                        selectedDate = day
                        updateAvailableHoursText(selectedDate)
                        fetchAppointmentsForSelectedDate(getFormattedDate(day))
                    }
                }
            }
            weekCalendar.addView(dayView)
        }
    }

    private fun selectCurrentDayAndFetchAppointments() {
        val currentDay = Calendar.getInstance()
        val today = getFormattedDate(currentDay)

        for (i in 0..6) {
            val day = weekStart.clone() as Calendar
            day.add(Calendar.DAY_OF_MONTH, i)

            if (isSameDay(currentDay, day)) {
                clearSelection()
                val dayView = weekCalendar.getChildAt(i)
                dayView?.setBackgroundResource(R.drawable.day_selector)

                updateAvailableHoursText(currentDay)

                fetchAppointmentsForSelectedDate(today)
                break
            }
        }
    }

    private fun isSameDay(day1: Calendar, day2: Calendar): Boolean {
        return day1.get(Calendar.YEAR) == day2.get(Calendar.YEAR) &&
                day1.get(Calendar.DAY_OF_YEAR) == day2.get(Calendar.DAY_OF_YEAR)
    }

    private fun clearSelection() {
        for (i in 0 until weekCalendar.childCount) {
            val child = weekCalendar.getChildAt(i)
            child.setBackgroundResource(android.R.color.transparent)
        }
    }

    private fun updateAvailableHoursText(selectedDate: Calendar) {
        val formattedDate = getFormattedDateForText(selectedDate)
        val fullText = " $formattedDate."

        findViewById<TextView>(R.id.txtAvailableHours).text = fullText
    }

    private fun getFormattedDateForText(calendar: Calendar): String {
        val day = calendar.get(Calendar.DAY_OF_MONTH).toString()
        val month = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale("ro", "RO"))?.lowercase()
        val year = calendar.get(Calendar.YEAR)

        return "$day $month $year"
    }

    private fun isAfterCurrentTime(hour: String): Boolean {
        val currentTime = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
        }

        val hourParts = hour.split(":")
        val appointmentTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourParts[0].toInt())
            set(Calendar.MINUTE, hourParts[1].toInt())
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return appointmentTime.after(currentTime)
    }

}