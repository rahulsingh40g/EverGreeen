package com.example.evergreen.firebase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.evergreen.activities.MainActivity
import com.example.evergreen.activities.SignInActivity
import com.example.evergreen.activities.SignUpActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FirebaseAuthClass() {
    private lateinit var auth: FirebaseAuth

    fun signUp(email :String, password : String,activity : SignUpActivity) {
        auth = Firebase.auth

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                OnCompleteListener<AuthResult> { task ->
                    // If the registration is successfully done
                    if (task.isSuccessful) {
                        // Firebase registered user
                        val user = auth.currentUser
                        val emailId = user?.email
                        Toast.makeText(activity, "You signed up successfully with $emailId", Toast.LENGTH_LONG).show()

                        activity.userRegisteredSuccess()

                    /*
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        // Registered Email
                        val registeredEmail = firebaseUser.email!!

                        val user = User(
                            firebaseUser.uid, name, registeredEmail


                        )

                        // call the registerUser function of FirestoreClass to make an entry in the database.
                        FirestoreClass().registerUser(this@SignUpActivity, user)

                        */

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
                    activity.signInSuccess()
                    // Calling the FirestoreClass signInUser function to get the data of user from database.
                    //FirestoreClass().loadUserData(this@SignInActivity)
                } else {
                    Toast.makeText(
                        activity,
                        task.exception!!.message,
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("signinerror" , "${task.exception!!.message}")
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
}