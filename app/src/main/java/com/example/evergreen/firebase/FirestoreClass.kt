package com.example.evergreen.firebase

import android.app.Activity
import android.util.Log
import com.example.evergreen.activities.BaseActivity
import com.example.evergreen.activities.SignInActivity
import com.example.evergreen.activities.SignUpActivity
import com.example.evergreen.model.User
import com.example.evergreen.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity : SignUpActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS)
            // Document ID for users fields. Here the document it is the User ID.
            .document(userInfo.uid)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                // Here call a function of base activity for transferring the result to it.
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document",
                    e
                )
            }
    }

    fun loadUserData(activity: SignInActivity, readBoardsList: Boolean = false) {

        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.USERS)
            // The document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())

                // Here we have received the document snapshot which is converted into the User Data model object.
                val loggedInUser = document.toObject(User::class.java)!!

                // Here call a function of base activity for transferring the result to it.
                activity.signInSuccess(loggedInUser)

                /* when (activity) {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser, readBoardsList)
                    }
                    is MyProfileActivity -> {
                        activity.setUserDataInUI(loggedInUser)
                    }
                }
                */
            }
            .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.
                  activity.hideProgressDialog()
//                when (activity) {
//                    is SignInActivity -> {
//                        activity.hideProgressDialog()
//                    }
//                    is MainActivity -> {
//                        activity.hideProgressDialog()
//                    }
//                    is MyProfileActivity -> {
//                        activity.hideProgressDialog()
//                    }
//                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting loggedIn user details",
                    e
                )
            }
    }


    fun getCurrentUserID(): String {
        return FirebaseAuthClass().getCurrentUserID()
    }
}