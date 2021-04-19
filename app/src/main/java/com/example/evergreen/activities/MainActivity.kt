package com.example.evergreen.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.example.evergreen.R
import com.example.evergreen.firebase.FirebaseAuthClass
import com.example.evergreen.firebase.FirestoreClass
import com.example.evergreen.model.User
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.io.IOException

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    // A global variable for User Name
    private lateinit var mUserName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBar()

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().loadUserData(this@MainActivity, true)

        // Assign the NavigationView.OnNavigationItemSelectedListener to navigation view.
        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_main_activity)
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        toolbar_main_activity.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer() {

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_my_profile -> {

                startActivityForResult(
                        Intent(this@MainActivity, EditProfileActivity::class.java),
                        EDIT_PROFILE_REQUEST_CODE
                )
            }

            R.id.nav_sign_out -> {
                // Here sign outs the user from firebase in this device.
                if(FirebaseAuthClass().getCurrentUserID().isNotEmpty())
                    FirebaseAuthClass().signOut(this)

                // Send the user to the intro screen of the application.
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK
            && requestCode == EDIT_PROFILE_REQUEST_CODE
        ) {
            // Get the user updated details.
                showProgressDialog(resources.getString(R.string.please_wait))
            Log.i("main", "call for load")
            FirestoreClass().loadUserData(this@MainActivity)
        }
//        else if (resultCode == Activity.RESULT_OK
//            && requestCode == CREATE_BOARD_REQUEST_CODE
//        )
//        {
//            // Get the latest boards list.
//            FirestoreClass().getBoardsList(this@MainActivity)
//        }
        else {
            Log.e("Cancelled", "Cancelled")
        }
    }

    fun updateNavigationUserDetails(user: User, readBoardsList: Boolean) {

        hideProgressDialog()

        mUserName = user.name

        // The instance of the header view of the navigation view.
        val headerView = nav_view.getHeaderView(0)

        // The instance of the user image of the navigation view.
        val navUserImage = headerView.findViewById<ImageView>(R.id.iv_user_image)

        // Load the user image in the ImageView.
        try {
            Glide
                    .with(this@MainActivity)
                    .load(user.image) // URL of the image
                    .centerCrop() // Scale type of the image.
                    .placeholder(R.drawable.ic_user_place_holder) // A default place holder
                    .into(navUserImage) // the view in which the image will be loaded.
            Log.i("main", " done with glide in nav with ${user.image}")
        }catch(e : IOException){
            Log.e("exc" , "${e.printStackTrace()}")
        }

        // The instance of the user name TextView of the navigation view.
        val navUsername = headerView.findViewById<TextView>(R.id.tv_username)
        // Set the user name
        navUsername.text = user.name

//        if (readBoardsList) {
//            // Show the progress dialog.
//            showProgressDialog(resources.getString(R.string.please_wait))
//            FirestoreClass().getBoardsList(this@MainActivity)
//        }
    }

    companion object {
        //A unique code for starting the activity for result
        const val EDIT_PROFILE_REQUEST_CODE: Int = 11

        const val CREATE_BOARD_REQUEST_CODE: Int = 12
    }

}