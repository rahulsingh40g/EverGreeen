package com.example.evergreen.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.example.evergreen.R
import com.example.evergreen.firebase.FirebaseAuthClass
import com.example.evergreen.firebase.FirestoreClass
import com.example.evergreen.model.User
import com.example.evergreen.utils.Constants
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.app_bar_main.*

class DashboardActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        setupActionBar()

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().loadUserData(this)

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
            startActivityForResult(intent, UPDATE_PLANTED_COUNT)
        }
        cv_view_accomplishments.setOnClickListener {
            val intent = Intent(this, PlantedMyMeActivity::class.java)
            startActivity(intent)
        }
    }

    fun updateCount(mUser: User){
        hideProgressDialog()
        var countOfPlants = mUser.planted_count
        var accomplishmentString ="You have planted at $countOfPlants spots till date."
        if(countOfPlants == 0){
            cv_view_accomplishments.visibility = View.GONE
            iv_planted_greater_than_zero.visibility = View.GONE
        }
        else if(countOfPlants == 1){
            iv_planted_greater_than_zero.visibility = View.VISIBLE
            cv_view_accomplishments.visibility = View.VISIBLE
            accomplishmentString = "You have planted at $countOfPlants spot till date."
            accomplishmentString += " Keep up the good work."
        }
        else{
            iv_planted_greater_than_zero.visibility = View.VISIBLE
            cv_view_accomplishments.visibility = View.VISIBLE
            accomplishmentString += " Keep up the good work."
        }
        tv_planted_count.text =  accomplishmentString

    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_dashboard_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = "DASHBOARD"
        }

        toolbar_dashboard_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == UPDATE_PLANTED_COUNT
        ) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().loadUserData(this)
        }

    }

    companion object{
        const val UPDATE_PLANTED_COUNT: Int = 203
    }

}