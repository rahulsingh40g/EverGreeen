package com.example.evergreen.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.evergreen.R
import com.example.evergreen.firebase.FirebaseAuthClass
import com.example.evergreen.firebase.FirestoreClass
import com.example.evergreen.model.Post
import com.example.evergreen.utils.Constants
import kotlinx.android.synthetic.main.activity_book_cum_approve_spot.*
import java.io.IOException

class BookCumApproveSpotActivity : BaseActivity() {
    private var isApprove : Boolean = false
    private var selectedStatus : String = ""

    private lateinit var mPostDetails : Post
    private lateinit var mPostedByName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_cum_approve_spot)
        if(intent.hasExtra(Constants.POST_DETAIL))
            mPostDetails = intent.getParcelableExtra<Post>(Constants.POST_DETAIL)!!

        if(intent.hasExtra(Constants.POSTEDBYNAME))
            mPostedByName = intent.getStringExtra(Constants.POSTEDBYNAME)!!

        if(intent.hasExtra(Constants.BYADMIN)) {
            isApprove = intent.getBooleanExtra(Constants.BYADMIN, false)
            setupUI()
        }
    }

    private fun setupUI() {

        setSupportActionBar(toolbar_bookspot_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_bookspot_activity.setNavigationOnClickListener { onBackPressed() }

        if(isApprove){
            btn_book_this_spot.visibility = View.GONE
            ll_btns_for_approve.visibility = View.VISIBLE

            if(actionBar != null){
                actionBar.title = "Spot Approval"
            }
            btn_reject_this_spot.setOnClickListener{
                if(et_description_byPlanter_cum_admin_bookspot.text.toString().isEmpty()){
                    showErrorSnackBar("Please provide a valid reason for rejection.")
                }else {
                    selectedStatus = Constants.SPOT_REJECTED
                    showAlertDialog(
                        this, "Please consider your choice wisely," +
                                " Actions can't be undone and this spot will be deleted forever"
                    )
                }
            }
            btn_approve_this_spot.setOnClickListener{
                selectedStatus = Constants.SPOT_OPEN_FOR_BOOKING
                showAlertDialog(this, "Spot will be open for planting!!")
            }
            displayPostDetails()
        }else{
            btn_book_this_spot.visibility = View.VISIBLE
            ll_btns_for_approve.visibility = View.GONE
            et_description_byPlanter_cum_admin_bookspot.visibility = View.GONE
            if(actionBar != null){
                actionBar.title = "Spot Booking"
            }
            btn_book_this_spot.setOnClickListener{
                selectedStatus = Constants.SPOT_BOOKED
                showAlertDialog(this,"This spot will be booked then you must plant within 5 days," +
                        " otherwise spot will be locked against you for 2 days")
            }
            displayPostDetails()
        }
    }

    private fun updatePostDetails(status : String) {
        mPostDetails.status = status
        mPostDetails.bookedBy = FirebaseAuthClass().getCurrentUserID()
        if(isApprove)  mPostDetails.descriptionByAdmin = et_description_byPlanter_cum_admin_bookspot.text.toString()

        showProgressDialog(resources.getString(R.string.please_wait))
        if(isApprove) FirestoreClass().updatePostDetails(this, mPostDetails, true)
            else FirestoreClass().updatePostDetails(this, mPostDetails, false)
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
        if(isApprove) Toast.makeText(this,"Spot status changed successfully!", Toast.LENGTH_SHORT).show()
            else Toast.makeText(this,"Spot Booked Successfully!", Toast.LENGTH_SHORT).show()
        setResult(RESULT_OK)
        finish()
    }

    fun onYesAlert() {
       updatePostDetails(selectedStatus)
    }
}