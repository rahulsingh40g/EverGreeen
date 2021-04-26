package com.example.evergreen.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.evergreen.activities.SignInActivity
import com.example.evergreen.activities.SignUpActivity
import com.example.evergreen.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FirebaseAuthClass() {
    private lateinit var auth: FirebaseAuth

    fun signUp(user : User, password: String, activity : SignUpActivity){
        val email = user.email
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                OnCompleteListener<AuthResult> { task ->
                    // If the registration is successfully done
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val registeredEmail = firebaseUser.email!!
                        user.uid = firebaseUser.uid
                        // call the registerUser function of FirestoreClass to make an entry in the database.
                        FirestoreClass().registerUser(activity, user)

                    } else {
                        Toast.makeText(
                            activity,
                            task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("signuperror" , "${task.exception!!.message}")
                        activity.hideProgressDialog()
                    }

                }
            )
        }



    fun signIn(email: String, password: String, activity : SignInActivity) {
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Calling the FirestoreClass signInUser function to get the data of user from database.
                        FirestoreClass().loadAdminOrUserData(activity)
                } else {
                    Toast.makeText(
                        activity,
                        task.exception!!.message,
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("signinerror", "${task.exception!!.message}")
                    activity.hideProgressDialog()
                }
            }
    }








    fun signOut(activity : Activity){
        auth = Firebase.auth
        auth.signOut()
    }

    fun getCurrentUserID(): String {
       auth = Firebase.auth
        val user = auth.currentUser
        if(user != null){
            return user.uid
        }
        return ""
    }
    fun getCurrentUserMailId():String{
        auth = Firebase.auth
        var user = auth.currentUser
        if(user != null) return user.email
        return ""
    }
}