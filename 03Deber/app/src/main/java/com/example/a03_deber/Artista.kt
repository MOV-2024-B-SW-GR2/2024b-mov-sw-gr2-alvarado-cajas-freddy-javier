package com.example.a03_deber

import android.os.Parcel
import android.os.Parcelable

class Artista (
    val id: Int,
    val nombre: String,
    val descripcion: String
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        if (parcel.readString()==null) "" else parcel.readString()!!,
        if (parcel.readString()==null) "" else parcel.readString()!!,
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nombre)
        parcel.writeString(descripcion)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Artista> {
        override fun createFromParcel(parcel: Parcel): Artista {
            return Artista(parcel)
        }

        override fun newArray(size: Int): Array<Artista?> {
            return arrayOfNulls(size)
        }
    }

}