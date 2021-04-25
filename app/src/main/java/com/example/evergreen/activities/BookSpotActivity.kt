package com.example.evergreen.activities

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.evergreen.R
import com.example.evergreen.firebase.FirebaseAuthClass
import com.example.evergreen.firebase.FirestoreClass
import com.example.evergreen.model.Post
import com.example.evergreen.utils.Constants
import kotlinx.android.synthetic.main.activity_book_spot.*
import java.io.IOException

class BookSpotActivity : BaseActivity() {
    private lateinit var mPostDetails : Post
    private lateinit var mPostedByName : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_spot)
        setupActionBar()
        if(intent.hasExtra(Constants.POST_DETAIL))
            mPostDetails = intent.getParcelableExtra<Post>(Constants.POST_DETAIL)!!

        if(intent.hasExtra(Constants.POSTEDBYNAME))
            mPostedByName = intent.getStringExtra(Constants.POSTEDBYNAME)!!

        displayPostDetails()
        btn_book_this_spot.setOnClickListener{
            showAlertDialog(this,"This spot will be booked then you must plant within 5 days," +
                    " otherwise spot will be locked against you for 2 days")
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_bookspot_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = "Book Spot"
        }

        toolbar_bookspot_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun updatePostDetails() {
        mPostDetails.status = Constants.SPOT_BOOKED
        mPostDetails.bookedBy = FirebaseAuthClass().getCurrentUserID()
        mPostDetails.descriptionByPlanter = et_description_byPlanter_bookspot.text.toString()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().updatePostDetails(this, mPostDetails)
     }

    private fun displayPostDetails() {
        try {
            Glide
                .with(this)
                .load(mPostDetails.imageBefore) // URL of the image
                .centerCrop() // Scale type of the image.
                .placeholder(R.drawable.ic_post_image_150) // A default place holder
                .into(iv_place_image_bookspot) // the view in which the image will be loaded.
        }catch (e: IOException){
            Log.e("exc", "${e.printStackTrace()}")
        }
        tv_posted_by_bookspot.text = mPostedByName
        tv_location_bookspot.text = mPostDetails.location
        if(mPostDetails.descriptionByCreator.isNotEmpty())
            tv_description_byCreator_bookspot.text = mPostDetails.descriptionByCreator
        else
            tv_description_byCreator_bookspot.text = "No description available"
    }

    fun onUpdateSuccess() {
        hideProgressDialog()
        Toast.makeText(this,"Spot Booked Successfully!", Toast.LENGTH_SHORT).show()
        setResult(RESULT_OK)
        finish()
    }

    fun onYesAlert() {
        updatePostDetails()
    }
}