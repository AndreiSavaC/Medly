package com.example.androidapp.models

import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.util.Date

class UserInfo {
    var firstName:String? = null;
    var lastName:String? = null;
    var email:String? = null;
    var gender:String? = null;
    var height:Float? = null;
    var weight:Float? = null;
    var bDay:LocalDate? = null;

    fun GetAge() : Int{

        // Get the current date
        val currentDate = LocalDate.now()

        // Calculate the period between the birth date and current date
        val age = Period.between(bDay,currentDate).years

        return age
    }
}