package com.example.evergreen.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.evergreen.R
import com.example.evergreen.firebase.FirebaseAuthClass
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.app_bar_main.*

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        setupActionBar()

        cv_approval_status.setOnClickListener{
            val intent = Intent(this, ApprovalStatusActivity::class.java)
            startActivity(intent)
        }
        cv_plantedStatus.setOnClickListener{
            val intent = Intent(this, PlantedStatusActivity::class.java)
            startActivity(intent)
        }
        cv_booked_spots.setOnClickListener{
            val intent = Intent(this, BookedSpotsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_dashboard_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_dashboard_activity.setNavigationOnClickListener { onBackPressed() }
    }

}