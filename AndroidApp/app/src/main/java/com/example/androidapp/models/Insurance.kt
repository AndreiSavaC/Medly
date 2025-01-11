package com.example.androidapp.models

import android.os.Parcel
import android.os.Parcelable
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class Insurance(
    val insuranceCode: Double,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val birthday: String,
    val doctorId: Int
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(insuranceCode)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(gender)
        parcel.writeString(birthday)
        parcel.writeInt(doctorId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Insurance> {
        override fun createFromParcel(parcel: Parcel): Insurance {
            return Insurance(parcel)
        }

        override fun newArray(size: Int): Array<Insurance?> {
            return arrayOfNulls(size)
        }
    }

    fun getAge(): Int {
        val birthDate = LocalDate.parse(birthday, DateTimeFormatter.ISO_DATE)
        val currentDate = LocalDate.now()
        return Period.between(birthDate, currentDate).years
    }
}
