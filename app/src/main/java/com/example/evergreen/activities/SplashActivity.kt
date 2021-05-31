package com.example.evergreen.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Toast
import com.example.evergreen.R
import com.example.evergreen.firebase.FirebaseAuthClass
import com.example.evergreen.firebase.FirestoreClass
import com.example.evergreen.model.Admin
import com.example.evergreen.model.User
import com.example.evergreen.utils.Constants
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val typeface: Typeface = Typeface.createFromAsset(assets, "carbon bl.ttf")
        tv_app_name.typeface = typeface

        Handler().postDelayed({
            val currentUserID = FirebaseAuthClass().getCurrentUserID()

            if (currentUserID.isNotEmpty()) {
                FirestoreClass().loadAdminOrUserData(this)
            } else {
                startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
                finish()
            }
        }, 300) // changed time for testing purpose
    }

    fun signInSuccessUser(user: User) {
        Toast.makeText(this, "${user.name} signed in successfully.", Toast.LENGTH_LONG).show()
        intent = Intent(this, MainActivity::class.java)
        intent.putExtra(Constants.USER_DETAIL, user)
        startActivity(intent)
        this.finish()
    }

    fun signInSuccessByAdmin(loggedInAdmin: Admin) {
        Toast.makeText(this, "${loggedInAdmin.email} signed in successfully.", Toast.LENGTH_SHORT).show()
        intent = Intent(this, MainActivity::class.java)
        intent.putExtra(Constants.ADMIN_DETAIL, loggedInAdmin)
        startActivity(intent)
        this.finish()
    }
}