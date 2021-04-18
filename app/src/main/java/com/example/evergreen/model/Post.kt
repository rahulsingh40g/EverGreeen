package com.example.evergreen.model

import android.os.Parcel
import android.os.Parcelable
import com.example.evergreen.utils.Constants

data class Post (
    val docId : String ="",
    var location :String= "",
    var imageBefore : String ="",
    var imageAfter : String = "",
    val postedBy: String ="",
    var bookedBy: String="",
    var status : String = Constants.SPOT_UNDER_REVIEW
) :Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) =with(parcel) {
        parcel.writeString(docId)
        parcel.writeString(location)
        parcel.writeString(imageBefore)
        parcel.writeString(imageAfter)
        parcel.writeString(postedBy)
        parcel.writeString(bookedBy)
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }
}