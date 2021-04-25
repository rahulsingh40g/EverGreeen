package com.example.evergreen.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.evergreen.R
import com.example.evergreen.adapters.BookedSpotsAdapter
import com.example.evergreen.adapters.PlantedPostsAdapter
import com.example.evergreen.firebase.FirestoreClass
import com.example.evergreen.model.Post
import com.example.evergreen.model.User
import com.example.evergreen.utils.Constants
import kotlinx.android.synthetic.main.activity_booked_spots.*
import kotlinx.android.synthetic.main.activity_planted_my_me.*

class PlantedMyMeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planted_my_me)

        setupActionBar()
        showProgressDialog("Please wait...")
        FirestoreClass().loadUserData(this)
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_planted_by_me_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_planted_by_me_activity.setNavigationOnClickListener { onBackPressed() }
    }



    fun populateRV(postsList: ArrayList<Post>) {
        hideProgressDialog()
        Log.i("2posts_Populate","displaying post before but serial thing + ${postsList.size} ")
        if (postsList.size > 0) {
            Log.i("2posts","displaying posts for rv approved ")
            rv_planted_by_me_list.visibility = View.VISIBLE
            tv_no_posts_available_planted_by_me.visibility = View.GONE

            rv_planted_by_me_list.layoutManager = LinearLayoutManager(this@PlantedMyMeActivity)
            rv_planted_by_me_list.setHasFixedSize(true)

            val adapter = PlantedPostsAdapter(this,postsList)
            rv_planted_by_me_list.adapter = adapter
        } else {
            rv_planted_by_me_list.visibility = View.GONE
            tv_no_posts_available_planted_by_me.visibility = View.VISIBLE
        }
    }

}