package com.example.evergreen.activities

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.evergreen.R
import com.example.evergreen.adapters.ApprovedPostsAdapter
import com.example.evergreen.adapters.PlantedPostsAdapter
import com.example.evergreen.firebase.FirestoreClass
import com.example.evergreen.model.Post
import com.example.evergreen.utils.Constants
import kotlinx.android.synthetic.main.activity_planted_status.*
import kotlinx.android.synthetic.main.item_post_approved.*

class PlantedStatusActivity : BaseActivity() {
    lateinit var status :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planted_status)

        setupActionBar()
        status = Constants.SPOT_OPEN_FOR_BOOKING
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getPostsFromStatusValue(Constants.SPOT_OPEN_FOR_BOOKING,this)
        //Log.i("1posts","displaying post before but serial thing + ${posts.size} ")
//        Log.i("1posts","hello")
//        getPosts(Constants.SPOT_OPEN_FOR_BOOKING)
//        Log.i("1posts","hello2")
        bottomNavigationView_planted_status.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.bn_open_for_booking->{
                    status = Constants.SPOT_OPEN_FOR_BOOKING
                    showProgressDialog(resources.getString(R.string.please_wait))
                    FirestoreClass().getPostsFromStatusValue(Constants.SPOT_OPEN_FOR_BOOKING,this)
                }
                R.id.bn_booked->{
                    status = Constants.SPOT_BOOKED
                    showProgressDialog(resources.getString(R.string.please_wait))
                    FirestoreClass().getPostsFromStatusValue(Constants.SPOT_BOOKED,this)
                }
                R.id.bn_planted->{
                    status = Constants.SPOT_PLANTED
                    showProgressDialog(resources.getString(R.string.please_wait))
                    FirestoreClass().getPostsFromStatusValue(Constants.SPOT_PLANTED,this)
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
            actionBar.title = "PLANTED STATUS"
        }

        toolbar_planted_status_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.reload_option, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.refresh ->{
                val selectedItem = bottomNavigationView_planted_status.menu.findItem(bottomNavigationView_planted_status.selectedItemId)
                when(selectedItem.itemId){
                    R.id.bn_open_for_booking->{
                        status = Constants.SPOT_OPEN_FOR_BOOKING
                        showProgressDialog(resources.getString(R.string.please_wait))
                        FirestoreClass().getPostsFromStatusValue(Constants.SPOT_OPEN_FOR_BOOKING,this)
                    }
                    R.id.bn_booked->{
                        status = Constants.SPOT_BOOKED
                        showProgressDialog(resources.getString(R.string.please_wait))
                        FirestoreClass().getPostsFromStatusValue(Constants.SPOT_BOOKED,this)
                    }
                    R.id.bn_planted->{
                        status = Constants.SPOT_PLANTED
                        showProgressDialog(resources.getString(R.string.please_wait))
                        FirestoreClass().getPostsFromStatusValue(Constants.SPOT_PLANTED,this)
                    }
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun populateRV(postsList: ArrayList<Post>,creators : ArrayList<String>, planters : ArrayList<String>) {
        hideProgressDialog()
        Log.i("2posts_Populate","displaying post before but serial thing + ${postsList.size} ")
        if (postsList.size > 0) {
            Log.i("2posts","displaying posts for rv approved ")
            rv_planted_posts_list.visibility = View.VISIBLE
            tv_no_posts_available_planted_status_activity.visibility = View.GONE

            rv_planted_posts_list.layoutManager = LinearLayoutManager(this@PlantedStatusActivity)
            rv_planted_posts_list.setHasFixedSize(true)

            val adapter = ApprovedPostsAdapter(this, postsList,creators,planters,status)
            rv_planted_posts_list.adapter = adapter
        } else {
            rv_planted_posts_list.visibility = View.GONE
            tv_no_posts_available_planted_status_activity.visibility = View.VISIBLE
        }
    }

    fun populateRvPlanted(postsList: ArrayList<Post>, creators : ArrayList<String>, planters : ArrayList<String>) {
        hideProgressDialog()
        Log.i("3posts_Populate","displaying post before but serial thing + ${postsList.size} ")
        if (postsList.size > 0) {
            Log.i("3posts","displaying posts for rv approved ")
            rv_planted_posts_list.visibility = View.VISIBLE
            tv_no_posts_available_planted_status_activity.visibility = View.GONE

            rv_planted_posts_list.layoutManager = LinearLayoutManager(this@PlantedStatusActivity)
            rv_planted_posts_list.setHasFixedSize(true)

            val adapter = PlantedPostsAdapter(this, postsList,creators,planters)
            rv_planted_posts_list.adapter = adapter
        } else {
            rv_planted_posts_list.visibility = View.GONE
            tv_no_posts_available_planted_status_activity.visibility = View.VISIBLE
        }
    }

}