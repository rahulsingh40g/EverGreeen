package com.example.evergreen.model

import android.os.Parcel
import android.os.Parcelable
import com.example.evergreen.utils.Constants

data class Post(
        val postId : String ="",
        var location :String= "",
        var city :String = "",
        var state : String = "", // bigger locality
        var imageBefore : String ="",
        var imageAfter : String = "",
        var postedBy: String ="",
        var bookedBy: String="",
        var postedByName: String ="",
        var bookedByName: String="",
        var status : String = Constants.SPOT_UNDER_REVIEW,
        var isrejected : String = "false", // rejected during approval of post
        var descriptionByCreator : String = "description",
        var descriptionByAdmin : String = "description", // for rejection during approval of post
        var descriptionByPlanter : String = "description" //

) :Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
            parcel.readString()!!,
        parcel.readString()!!,     // state
        parcel.readString()!!,
        parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!, // booked by
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) =with(parcel) {
        parcel.writeString(postId)
        parcel.writeString(location)
        parcel.writeString(city)
        parcel.writeString(state)
        parcel.writeString(imageBefore)
        parcel.writeString(imageAfter)
        parcel.writeString(postedBy)
        parcel.writeString(bookedBy)
        parcel.writeString(postedByName)
        parcel.writeString(bookedByName)
        parcel.writeString(status)
        parcel.writeString(isrejected)
        parcel.writeString(descriptionByCreator)
        parcel.writeString(descriptionByAdmin)
        parcel.writeString(descriptionByPlanter)

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