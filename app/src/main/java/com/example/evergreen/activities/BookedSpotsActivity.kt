package com.example.evergreen.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.evergreen.R
import com.example.evergreen.adapters.BookedSpotsAdapter
import com.example.evergreen.firebase.FirestoreClass
import com.example.evergreen.model.Post
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_booked_spots.*

class BookedSpotsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booked_spots)

        setupActionBar()

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().loadUserData(this)
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_booked_spots_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = "Booked Spots"
        }

        toolbar_booked_spots_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.reload_option, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.refresh ->{
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().loadUserData(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun populateRV(postsList: ArrayList<Post>, creators : ArrayList<String>) {
        hideProgressDialog()
        if (postsList.size > 0) {
            rv_booked_spots_list.visibility = View.VISIBLE
            tv_no_posts_available_booked_spots.visibility = View.GONE

            rv_booked_spots_list.layoutManager = LinearLayoutManager(this@BookedSpotsActivity)
            rv_booked_spots_list.setHasFixedSize(true)

            val adapter = BookedSpotsAdapter(this,postsList, creators)
            rv_booked_spots_list.adapter = adapter
//            adapter.setOnClickListener(object : BookedSpotsAdapter.OnClickListener {
//                override fun onClick(position: Int, model: Post) {
//                    val intent = Intent(this@BookedSpotsActivity, UploadImageAfterActivity::class.java)
//                    intent.putExtra(Constants.POST_DETAIL, model)
//                    startActivity(intent)
//                }
//            })
        } else {
            rv_booked_spots_list.visibility = View.GONE
            tv_no_posts_available_booked_spots.visibility = View.VISIBLE
        }
    }

    fun unBookSpotSuccess(){
        hideProgressDialog()
        Toast.makeText(this,"Spot Unbooked !!",Toast.LENGTH_LONG).show()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().loadUserData(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK
            && requestCode == UPLOAD_IMAGE_AFTER_CODE
        ) {
            showProgressDialog(resources.getString(R.string.please_wait))
            Log.i("main", "call for load")
            FirestoreClass().loadUserData(this@BookedSpotsActivity)
        }

    }

    companion object{
        const val UPLOAD_IMAGE_AFTER_CODE: Int = 202
    }
}