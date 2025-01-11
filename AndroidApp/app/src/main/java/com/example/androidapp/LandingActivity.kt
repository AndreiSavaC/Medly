    package com.example.androidapp

    import android.content.Intent
    import android.os.Bundle
    import android.view.View
    import android.widget.Button
    import android.widget.LinearLayout
    import android.widget.TextView
    import androidx.appcompat.app.AppCompatActivity

    class LandingActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_landing)

            val btnBookAppointment = findViewById<Button>(R.id.btnBookAppointment)
            val btnEditProfile = findViewById<Button>(R.id.btnEditProfile)

            btnBookAppointment.setOnClickListener {
                val intent = Intent(this, ScheduleActivity::class.java)
                startActivity(intent)
            }

            btnEditProfile.setOnClickListener {
                val intent = Intent(this, DViewActivity::class.java)
                startActivity(intent)
            }

            val appointmentList = findViewById<LinearLayout>(R.id.appointment_list)
            val appointments = listOf(
                Pair("Dr. Sarah Johnson", "March 28, 2024 - 10:30 AM"),
                Pair("Dr. Mike Peterson", "April 2, 2024 - 1:00 PM"),
                Pair("Dr. Emily Davis", "April 10, 2024 - 9:00 AM")
            )

            for ((index, appointment) in appointments.withIndex()) {
                val (doctor, date) = appointment

                val appointmentItem = TextView(this).apply {
                    text = "$doctor\n$date"
                    textSize = 14f
                    setPadding(8, 8, 8, 8)
                }
                appointmentList.addView(appointmentItem)

                if (index < appointments.size - 1) {
                    val separator = View(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            2
                        ).apply {
                            setMargins(0, 8, 0, 8)
                        }
                        setBackgroundColor(getColor(android.R.color.darker_gray))
                    }
                    appointmentList.addView(separator)
                }
            }

        }
    }
