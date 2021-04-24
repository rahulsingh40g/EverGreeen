package com.example.evergreen.model

import android.os.Parcel
import android.os.Parcelable

data class User (
    var uid : String ="",
    var name : String = "",
    var email : String ="",
    var location : String="",
    var city :String = "",
    var image :String = "",
    var mobile : Long = 0,
    var myPostIds : ArrayList<String> = ArrayList(),
    var bookedPostIds : ArrayList<String> = ArrayList(),
    var plantsBought : Int =0,
    var donationAmount : Int =0
) :Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
            parcel.readString()!!,

        parcel.readLong(),
        parcel.createStringArrayList()!!,
        parcel.createStringArrayList()!!,
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel){
        parcel.writeString(uid)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(location)
        parcel.writeString(city)

        parcel.writeString(image)
        parcel.writeLong(mobile)
        parcel.writeStringList(myPostIds)
        parcel.writeStringList(bookedPostIds)
        parcel.writeInt(plantsBought)
        parcel.writeInt(donationAmount)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}