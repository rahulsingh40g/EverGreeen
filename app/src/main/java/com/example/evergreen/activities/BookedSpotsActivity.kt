package com.example.evergreen.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.evergreen.R
import com.example.evergreen.adapters.ApprovedPostsAdapter
import com.example.evergreen.adapters.BookedSpotsAdapter
import com.example.evergreen.firebase.FirestoreClass
import com.example.evergreen.model.Post
import com.example.evergreen.utils.Constants
import kotlinx.android.synthetic.main.activity_booked_spots.*
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_planted_status.*

class BookedSpotsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booked_spots)

        setupActionBar()

        showProgressDialog("Please wait...")
        FirestoreClass().getApprovedPosts(Constants.SPOT_BOOKED,this)
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_booked_spots_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_booked_spots_activity.setNavigationOnClickListener { onBackPressed() }
    }

    fun populateRV(postsList: ArrayList<Post>) {
        hideProgressDialog()
        Log.i("2posts_Populate","displaying post before but serial thing + ${postsList.size} ")
        if (postsList.size > 0) {
            Log.i("2posts","displaying posts for rv approved ")
            rv_booked_spots_list.visibility = View.VISIBLE
            tv_no_posts_available_booked_spots.visibility = View.GONE

            rv_booked_spots_list.layoutManager = LinearLayoutManager(this@BookedSpotsActivity)
            rv_booked_spots_list.setHasFixedSize(true)

            val adapter = BookedSpotsAdapter(this,postsList)
            rv_booked_spots_list.adapter = adapter
        } else {
            rv_booked_spots_list.visibility = View.GONE
            tv_no_posts_available_booked_spots.visibility = View.VISIBLE
        }
    }

}