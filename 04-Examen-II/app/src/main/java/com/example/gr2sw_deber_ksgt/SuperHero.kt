package com.example.gr2sw_deber_ksgt

import android.os.Parcel
import android.os.Parcelable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SuperHero (
    val id: Int,
    val name: String,
    val isActive: Boolean,
    val debutDate: LocalDate,
    val popularity: Double,
    val latitude: Double,
    val longitude: Double
): Parcelable {
    val latitud: Double
        get() = latitude
    val longitud: Double
        get() = longitude

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        LocalDate.parse(parcel.readString(), DateTimeFormatter.ISO_DATE),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble()
    ) {
    }

    override fun toString(): String {
        return name
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeByte(if (isActive) 1 else 0)
        parcel.writeString(debutDate.format(DateTimeFormatter.ISO_DATE))
        parcel.writeDouble(popularity)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SuperHero> {
        override fun createFromParcel(parcel: Parcel): SuperHero {
            return SuperHero(parcel)
        }

        override fun newArray(size: Int): Array<SuperHero?> {
            return arrayOfNulls(size)
        }
    }
}