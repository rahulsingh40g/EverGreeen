package com.example.evergreen.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {


    const val POSTEDBYNAME: String = "posted_by_name"
    const val USER_DETAIL: String = "user_detail"
    const val POST_DETAIL : String = "post_detail"

    // Firebase Constants
    // This  is used for the collection name for USERS.
    const val USERS: String = "users"

    const val POSTS: String = "posts"


    // Firebase database field names
    const val UID  = "uid"
    const val IMAGE: String = "image"
    const val NAME: String = "name"
    const val MOBILE: String = "mobile"

    const val LOCATION: String = "location"
    const val MYPOSTIDS = "myPostIds"



    // for post
    const val POSTID  = "postId"
    const val FALSE = "false"
    const val TRUE = "true"
    const val STATUS = "status"

    const val CITY = "city"
    const val STATE = "state"





    const val ASSIGNED_TO: String = "assignedTo"
    const val DOCUMENT_ID: String = "documentId"
    const val TASK_LIST: String = "taskList"
    const val ID: String = "id"
    const val EMAIL: String = "email"

    const val BOARD_DETAIL: String = "board_detail"

    const val TASK_LIST_ITEM_POSITION: String = "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION: String = "card_list_item_position"

    const val BOARD_MEMBERS_LIST: String = "board_members_list"

    const val SELECT: String = "Select"
    const val UN_SELECT: String = "UnSelect"

    // Constants for status of post
    const val SPOT_UNDER_REVIEW = "under_review"
    const val SPOT_OPEN_FOR_BOOKING = "open_for_booking"
    const val SPOT_REJECTED = "rejected"
    const val SPOT_BOOKED = "booked"
    const val SPOT_PLANTED = "planted"


    //A unique code for asking the Read Storage Permission using this we will be check and identify in the method onRequestPermissionsResult
    const val READ_STORAGE_PERMISSION_CODE = 1
    // A unique code of image selection from Phone Storage.
    const val PICK_IMAGE_REQUEST_CODE = 2

    /**
     * A function for user profile image selection from phone storage.
     */
    fun showImageChooser(activity: Activity) {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    /**
     * A function to get the extension of selected image.
     */
    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        /*
         * MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
         *
         * getSingleton(): Get the singleton instance of MimeTypeMap.
         *
         * getExtensionFromMimeType: Return the registered extension for the given MIME type.
         *
         * contentResolver.getType: Return the MIME type of the given content URL.
         */
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}