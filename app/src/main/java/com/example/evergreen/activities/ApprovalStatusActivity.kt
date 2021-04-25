package com.example.evergreen.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.evergreen.R
import com.example.evergreen.adapters.ApprovedPostsAdapter
import com.example.evergreen.adapters.PostItemsAdapter
import com.example.evergreen.firebase.FirestoreClass
import com.example.evergreen.model.Post
import com.example.evergreen.utils.Constants
import kotlinx.android.synthetic.main.activity_approval_status.*
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.content_main.*

class ApprovalStatusActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("1posts","hello")
        setContentView(R.layout.activity_approval_status)

        setupActionBar()
        showProgressDialog("Please wait...")
        FirestoreClass().getApprovedPosts(Constants.SPOT_UNDER_REVIEW,this)
        //Log.i("1posts","displaying post before but serial thing + ${posts.size} ")
//        Log.i("1posts","hello")
//        getPosts(Constants.SPOT_OPEN_FOR_BOOKING)
//        Log.i("1posts","hello2")
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.bn_under_review->{
                    showProgressDialog("Please wait...")
                    FirestoreClass().getApprovedPosts(Constants.SPOT_UNDER_REVIEW,this)
                }
                R.id.bn_approved->{
                    showProgressDialog("Please wait...")
                    FirestoreClass().getApprovedPosts(Constants.SPOT_OPEN_FOR_BOOKING,this)
                }
                R.id.bn_rejected->{
                    showProgressDialog("Please wait...")
                    FirestoreClass().getApprovedPosts(Constants.SPOT_REJECTED,this)
                }
            }
            true
        }

    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_approval_status_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_approval_status_activity.setNavigationOnClickListener { onBackPressed() }
    }

    fun populateRV(postsList: ArrayList<Post>) {
        hideProgressDialog()
        Log.i("1posts_Populate","displaying post before but serial thing + ${postsList.size} ")
        if (postsList.size > 0) {
            Log.i("1posts","displaying posts for rv approved ")
            rv_approved_posts_list.visibility = View.VISIBLE
            tv_no_posts_available_approval_status_activity.visibility = View.GONE

            rv_approved_posts_list.layoutManager = LinearLayoutManager(this@ApprovalStatusActivity)
            rv_approved_posts_list.setHasFixedSize(true)

            val adapter = ApprovedPostsAdapter(this, postsList)
            rv_approved_posts_list.adapter = adapter
        } else {
            rv_approved_posts_list.visibility = View.GONE
            tv_no_posts_available_approval_status_activity.visibility = View.VISIBLE
        }
    }

}