package com.example.evergreen.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.evergreen.R
import com.example.evergreen.adapters.ApprovedPostsAdapter
import com.example.evergreen.adapters.PlantedPostsAdapter
import com.example.evergreen.firebase.FirestoreClass
import com.example.evergreen.model.Post
import com.example.evergreen.utils.Constants
import kotlinx.android.synthetic.main.activity_approval_status.*
import kotlinx.android.synthetic.main.activity_approval_status.toolbar_approval_status_activity
import kotlinx.android.synthetic.main.activity_planted_status.*

class PlantedStatusActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planted_status)

        setupActionBar()
        showProgressDialog("Please wait...")
        FirestoreClass().getApprovedPosts(Constants.SPOT_OPEN_FOR_BOOKING,this)
        //Log.i("1posts","displaying post before but serial thing + ${posts.size} ")
//        Log.i("1posts","hello")
//        getPosts(Constants.SPOT_OPEN_FOR_BOOKING)
//        Log.i("1posts","hello2")
        bottomNavigationView_planted_status.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.bn_open_for_booking->{
                    showProgressDialog("Please wait...")
                    FirestoreClass().getApprovedPosts(Constants.SPOT_OPEN_FOR_BOOKING,this)
                }
                R.id.bn_booked->{
                    showProgressDialog("Please wait...")
                    FirestoreClass().getApprovedPosts(Constants.SPOT_BOOKED,this)
                }
                R.id.bn_planted->{
                    showProgressDialog("Please wait...")
                    FirestoreClass().getApprovedPosts(Constants.SPOT_PLANTED,this)
                }
            }
            true
        }

    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_planted_status_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_planted_status_activity.setNavigationOnClickListener { onBackPressed() }
    }

    fun populateRV(postsList: ArrayList<Post>) {
        hideProgressDialog()
        Log.i("2posts_Populate","displaying post before but serial thing + ${postsList.size} ")
        if (postsList.size > 0) {
            Log.i("2posts","displaying posts for rv approved ")
            rv_planted_posts_list.visibility = View.VISIBLE
            tv_no_posts_available_planted_status_activity.visibility = View.GONE

            rv_planted_posts_list.layoutManager = LinearLayoutManager(this@PlantedStatusActivity)
            rv_planted_posts_list.setHasFixedSize(true)

            val adapter = ApprovedPostsAdapter(this, postsList)
            rv_planted_posts_list.adapter = adapter
        } else {
            rv_planted_posts_list.visibility = View.GONE
            tv_no_posts_available_planted_status_activity.visibility = View.VISIBLE
        }
    }

    fun populateRvPlanted(postsList: ArrayList<Post>) {
        hideProgressDialog()
        Log.i("3posts_Populate","displaying post before but serial thing + ${postsList.size} ")
        if (postsList.size > 0) {
            Log.i("3posts","displaying posts for rv approved ")
            rv_planted_posts_list.visibility = View.VISIBLE
            tv_no_posts_available_planted_status_activity.visibility = View.GONE

            rv_planted_posts_list.layoutManager = LinearLayoutManager(this@PlantedStatusActivity)
            rv_planted_posts_list.setHasFixedSize(true)

            val adapter = PlantedPostsAdapter(this, postsList)
            rv_planted_posts_list.adapter = adapter
        } else {
            rv_planted_posts_list.visibility = View.GONE
            tv_no_posts_available_planted_status_activity.visibility = View.VISIBLE
        }
    }

}