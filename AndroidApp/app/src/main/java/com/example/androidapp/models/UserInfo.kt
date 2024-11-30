package com.example.androidapp.models

import android.os.Parcel
import android.os.Parcelable
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class UserInfo(
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var gender: String? = null,
    var height: Float? = null,
    var weight: Float? = null,
    var birthDate: LocalDate? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readString()?.let { LocalDate.parse(it, DateTimeFormatter.ISO_DATE) }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(email)
        parcel.writeString(gender)
        parcel.writeValue(height)
        parcel.writeValue(weight)
        parcel.writeString(birthDate?.format(DateTimeFormatter.ISO_DATE))
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserInfo> {
        override fun createFromParcel(parcel: Parcel): UserInfo {
            return UserInfo(parcel)
        }

        override fun newArray(size: Int): Array<UserInfo?> {
            return arrayOfNulls(size)
        }
    }

    fun GetAge() : Int{
        val currentDate = LocalDate.now()
        val age = Period.between(birthDate,currentDate).years
        return age
    }
}