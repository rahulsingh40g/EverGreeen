package com.example.evergreen.model

import android.os.Parcel
import android.os.Parcelable




data class Admin (
        var uid: String = "",
        var email: String = "",
        var city: String = ""
        )  : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!
    )
            {
            }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(uid)
            parcel.writeString(email)
            parcel.writeString(city)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Admin> {
        override fun createFromParcel(parcel: Parcel): Admin {
            return Admin(parcel)
        }

        override fun newArray(size: Int): Array<Admin?> {
            return arrayOfNulls(size)
        }
    }
}
