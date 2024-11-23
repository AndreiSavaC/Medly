package com.example.androidapp.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun getFormattedWeekRange(start: Calendar, end: Calendar): String {
        val formatter = SimpleDateFormat("d MMM", Locale.getDefault())
        return "${formatter.format(start.time)} - ${formatter.format(end.time)}"
    }
}
