package com.example.evergreen.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.evergreen.R
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val typeface: Typeface =
            Typeface.createFromAsset(assets, "carbon bl.ttf")
        tv_app_name.typeface = typeface

        Handler().postDelayed({

            //TODO Firestore class for AutoLogin(Uncomment this and remove next line)
//            val currentUserID = FirestoreClass().getCurrentUserID()
//
//            if (currentUserID.isNotEmpty()) {
//                // Start the Main Activity
//                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
//            } else {
//                // Start the Intro Activity
//                startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
//            }

            startActivity(Intent(this@SplashActivity, IntroActivity::class.java))

            finish()
        }, 2500)
    }
}