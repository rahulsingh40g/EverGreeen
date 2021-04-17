package com.example.evergreen.model

import android.os.Parcel
import android.os.Parcelable

data class User(
        val id: String = "",
        val name: String = "",
        val city: String = "",
        val state: String = "",
        val email: String = "",
        val image: String = "",
        val mobile: Long = 0,
        val fcmToken: String = "",
):Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readLong(),
            parcel.readString()!!) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}