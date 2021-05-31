package com.example.evergreen.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.example.evergreen.R
import com.example.evergreen.firebase.FirebaseAuthClass
import com.example.evergreen.firebase.FirestoreClass
import com.example.evergreen.model.User
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.io.IOException

class SignUpActivity : BaseActivity() {
    private var mLatitude: Double = 0.0 // A variable which will hold the latitude value.
    private var mLongitude: Double = 0.0 // A variable which will hold the longitude value.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()
        btn_sign_up.setOnClickListener {
            registerUser()
        }
        tv_select_current_location_signUp.setOnClickListener {
            selectCurrentLocation(this)
        }
//        et_location_signUp.setOnClickListener{
//            try {
//                // These are the list of fields which we required is passed
//                val fields = listOf(
//                        Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG,
//                        Place.Field.ADDRESS
//                )
//                // Start the autocomplete intent with a unique request code.
//                val intent =
//                        Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
//                                .build(this)
//                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {

                val place: Place = Autocomplete.getPlaceFromIntent(data!!)

                et_location_signUp.setText(place.address)
                mLatitude = place.latLng!!.latitude
                mLongitude = place.latLng!!.longitude
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Cancelled", "Cancelled")
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_sign_up_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_sign_up_activity.setNavigationOnClickListener { onBackPressed() }
    }


    /**
     * A function to register a user to our app using the Firebase.
     * For more details visit: https://firebase.google.com/docs/auth/android/custom-auth
     */
    private fun registerUser() {
        // Here we get the text from editText and trim the space
        val name: String = et_name.text.toString().trim { it <= ' ' }
        val location: String = et_location_signUp.text.toString().trim { it <= ' ' }
        val email: String = et_email.text.toString().trim { it <= ' ' }
        val password: String = et_password.text.toString().trim { it <= ' ' }

        if (validateForm(name, location,email, password)) {
            val city : String = getCityFromBase()
            val user : User = User()
            user.email = email
            user.name = name
            user.location = location
            user.city = city
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuthClass().signUp(user ,password,this)
        }

    }

    /**
     * A function to validate the entries of a new user.
     */
    private fun validateForm(name: String, location :String,email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter name.")
                false
            }
            TextUtils.isEmpty(location) -> {
                showErrorSnackBar("Please enter location.")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter email.")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter password.")
                false
            }
            else -> {
                true
            }
        }
    }

    /**
     * A function to be called the user is registered successfully and entry is made in the firestore database.
     */
    fun userRegisteredSuccess() {

        // Hide the progress dialog
        hideProgressDialog()
        /**
         * Here the new user registered is automatically signed-in so we just sign-out the user from firebase
         * and send him to Intro Screen for Sign-In
         */
        //adding toast
        Toast.makeText(this,
            "${FirebaseAuthClass().getCurrentUserMailId()} has successfully registered with EverGreen!",
            Toast.LENGTH_LONG).show()
        FirebaseAuthClass().signOut(this)
        startActivity(Intent(this,SignInActivity::class.java))
        // Finish the Sign-Up Screen
        finish()
    }

    companion object {
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 3
    }

}