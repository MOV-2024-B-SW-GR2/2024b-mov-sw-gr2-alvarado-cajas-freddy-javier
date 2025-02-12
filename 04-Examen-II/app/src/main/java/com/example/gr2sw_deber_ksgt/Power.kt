package com.example.gr2sw_deber_ksgt

import android.os.Parcelable

class Power(
    val id: Int,
    val name: String,
    val description: String,
    val isOffensive: Boolean,
    val effectiveness: Double
): Parcelable {
    constructor(parcel: android.os.Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readDouble()
    ) {
    }

    override fun toString(): String {
        return name
    }

    override fun writeToParcel(parcel: android.os.Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeByte(if (isOffensive) 1 else 0)
        parcel.writeDouble(effectiveness)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Power> {
        override fun createFromParcel(parcel: android.os.Parcel): Power {
            return Power(parcel)
        }

        override fun newArray(size: Int): Array<Power?> {
            return arrayOfNulls(size)
        }
    }
}