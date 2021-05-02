package com.example.evergreen.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.evergreen.R
import com.example.evergreen.adapters.PostItemsAdapter
import com.example.evergreen.firebase.FirestoreClass
import com.example.evergreen.model.Admin
import com.example.evergreen.model.Post
import com.example.evergreen.model.User
import com.example.evergreen.utils.Constants
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.IOException


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var isFABOpen =false
    private var isAdminHere = false

    private lateinit var mAdmin : Admin
    private lateinit var mUser : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        setIntent(intent)
        handleIntent(intent)
        super.onNewIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        when {
            intent.hasExtra(Constants.ADMIN_DETAIL) -> {
                isAdminHere = true
                mAdmin = intent.getParcelableExtra<Admin>(Constants.ADMIN_DETAIL)!!
                setForAdmin()
            }
            intent.hasExtra(Constants.USER_DETAIL) ->{
                isAdminHere = false
                mUser = intent.getParcelableExtra<User>(Constants.USER_DETAIL)!!
                setForUser()
            }
            Intent.ACTION_SEARCH == intent.action -> {
                var query = intent.getStringExtra(SearchManager.QUERY)
                if(query != null) {
                    query = query.capitalize()
                    getPosts(query, true)
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun setForAdmin(){
        setSupportActionBar(toolbar_main_activity)
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        toolbar_main_activity.setNavigationOnClickListener {
            toggleDrawer()
        }
        nav_view.setNavigationItemSelectedListener(this)
        nav_view.menu[0].isVisible = false
        nav_view.getHeaderView(0).findViewById<TextView>(R.id.tv_username).text = "ADMIN at ${mAdmin.city}"

        getPosts(mAdmin.city, false)
        fab.visibility = View.GONE
        fab_donate.visibility = View.GONE
        fab_createPost.visibility = View.GONE
        fab_dashboard.visibility = View.GONE
        fab_shop.visibility = View.GONE
    }

    private fun setForUser(){
        setSupportActionBar(toolbar_main_activity)
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        toolbar_main_activity.setNavigationOnClickListener {
            toggleDrawer()
        }
        showProgressDialog(resources.getString(R.string.please_wait))
        updateNavigationUserDetails(mUser)

        // Assign the NavigationView.OnNavigationItemSelectedListener to navigation view.
        nav_view.setNavigationItemSelectedListener(this)
        nav_view.menu[1].isVisible = false

        //FAB listener
        fab.setOnClickListener {
            if(!isFABOpen){
                showFABMenu();
            }else{
                closeFABMenu();
            }
        }

        fab_createPost.setOnClickListener{
            val intent = Intent(this, CreatePostActivity::class.java)
            intent.putExtra(Constants.USER_DETAIL, mUser)
            startActivityForResult(
                    intent,
                    EDIT_PROFILE_REQUEST_CODE)
            closeFABMenu()
        }

        fab_dashboard.setOnClickListener{
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            closeFABMenu()
        }
        fab_donate.setOnClickListener{
            alertDialogDonation()
        }
        fab_shop.setOnClickListener{
            val intent = Intent(this@MainActivity, FreeShopActivity::class.java)
            intent.putExtra(Constants.USER_DETAIL, mUser)
            startActivityForResult(intent, FREE_SHOP_REQUEST_CODE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.refresh -> {
                if (isAdminHere) getPosts(mAdmin.city)
                else getPosts(mUser.city)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toggleDrawer() {

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }

    fun updateNavigationUserDetails(user: User) {
        hideProgressDialog()
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
        }catch (e: IOException){
            Log.e("exc", "${e.printStackTrace()}")
        }

        // The instance of the user name TextView of the navigation view.
        val navUsername = headerView.findViewById<TextView>(R.id.tv_username)
        // Set the user name
        navUsername.text = user.name
        getPosts(user.city)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_my_profile -> {
                if (!isAdminHere) {
                    startActivityForResult(
                            Intent(
                                    this@MainActivity, EditProfileActivity::class.java),
                            EDIT_PROFILE_REQUEST_CODE
                    )
                }
            }
            R.id.nav_donation -> {
                FirestoreClass().getDonationAmount(this)
            }
            R.id.nav_sign_out -> {
                // Here sign outs the user from firebase in this device.
                showAlertDialog(this@MainActivity, "You will be signed out and" +
                        " you won't be able to sign in automatically in future unless you sign in once again.")
            }
            R.id.nav_feedback -> {
                startActivity(Intent(this, FeedbackActivity::class.java))
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun getPosts(locality: String, isState: Boolean = false){
        showProgressDialog(resources.getString(R.string.please_wait))
        if(isAdminHere) FirestoreClass().getPostsFromLocality(this, locality, isState, Constants.SPOT_UNDER_REVIEW)
            else FirestoreClass().getPostsFromLocality(this, locality, isState, Constants.SPOT_OPEN_FOR_BOOKING)
    }

    fun displayPostsInUI(postsList: ArrayList<Post>, creators: ArrayList<String>) {
        hideProgressDialog()
        if (postsList.size > 0) {
            rv_posts_list.visibility = View.VISIBLE
            tv_no_posts_available.visibility = View.GONE

            rv_posts_list.layoutManager = LinearLayoutManager(this@MainActivity)
            rv_posts_list.setHasFixedSize(true)

            val adapter = PostItemsAdapter(this, postsList, creators)
            rv_posts_list.adapter = adapter
            adapter.setOnClickListener(object : PostItemsAdapter.OnClickListener {
                override fun onClick(position: Int, model: Post, postedByName: String) {
                    val intent = Intent(this@MainActivity, BookCumApproveSpotActivity::class.java)
                    intent.putExtra(Constants.POST_DETAIL, model)
                    intent.putExtra(Constants.POSTEDBYNAME, postedByName)
                    intent.putExtra(Constants.BYADMIN, isAdminHere)

                    if (isAdminHere) startActivityForResult(intent, APPROVE_SPOT_REQUEST_CODE)
                        else startActivityForResult(intent, BOOK_SPOT_REQUEST_CODE)
                }
            })
        } else {
            rv_posts_list.visibility = View.GONE
            tv_no_posts_available.visibility = View.VISIBLE
        }
    }

    private fun showFABMenu() {
        isFABOpen = true
        ll_createPost.animate().translationY(-resources.getDimension(R.dimen.standard_55))
        ll_dashboard.animate().translationY(-resources.getDimension(R.dimen.standard_105))
        ll_donate.animate().translationY(-resources.getDimension(R.dimen.standard_155))
        ll_shop.animate().translationY(-resources.getDimension(R.dimen.standard_205))

        tv_createPost.visibility= View.VISIBLE
        tv_dashboard.visibility= View.VISIBLE
        tv_donate.visibility= View.VISIBLE
        tv_shop.visibility= View.VISIBLE
    }

    private fun closeFABMenu() {
        isFABOpen = false
        ll_createPost.animate().translationY(0F)
        ll_dashboard.animate().translationY(0F)
        ll_donate.animate().translationY(0F)
        ll_shop.animate().translationY(0F)

        tv_createPost.visibility= View.GONE
        tv_dashboard.visibility= View.GONE
        tv_donate.visibility= View.GONE
        tv_shop.visibility= View.GONE
    }

    private fun alertDialogDonation() {
        val li = LayoutInflater.from(this)
        val promptsView: View = li.inflate(R.layout.alert_dialog_donation, null)
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(
                this, R.style.CustomAlertDialog)

        alertDialogBuilder.setView(promptsView)
        val userInput = promptsView.findViewById<View>(R.id.etUserInputDonation) as EditText

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Donate", DialogInterface.OnClickListener { dialog, id -> // get user input and set it to result
                    // edit text
                    val input = userInput.text.toString()
                    if(input.isNotEmpty()) {
                        val amount = input.toInt()
                        if (amount < 1 || amount > 10000) {
                            showErrorSnackBar(
                                "We don't accept that much money, " +
                                        "Kindly post your queries in feedback section."
                            )
                        } else {
                            FirestoreClass().donate(this, amount.toLong(), mUser)
                        }
                    }else{
                        showErrorSnackBar("Please enter some amount.")
                    }
                })
                .setNegativeButton("Cancel",
                        DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })

        // create alert dialog
        val alertDialog: AlertDialog = alertDialogBuilder.create()

        // show it
        alertDialog.show()
    }

    fun donationSuccess() {
        hideProgressDialog()
        Toast.makeText(this, "Thanks for donating, We really appreciate this :)", Toast.LENGTH_LONG).show()
    }

    fun getDonateSuccess(amount: String) {
        AlertDialog.Builder(this)
                .setTitle("Donation Amount")
                .setMessage("Donation amount collected till date is Rs. $amount.")
                .setPositiveButton("OK"){ dialog, _ ->
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel"){dialog , _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK
            && requestCode == EDIT_PROFILE_REQUEST_CODE
        ) {
                showProgressDialog(resources.getString(R.string.please_wait))
            Log.i("main", "call for load")
            FirestoreClass().loadUserData(this@MainActivity)
        } else if (resultCode == Activity.RESULT_OK
            && requestCode == CREATE_POST_REQUEST_CODE
        ) {
            // Get the latest posts list.
                Log.i("city", "muser city is ${mUser.city}")
                getPosts(mUser.city)
        } else if(resultCode == Activity.RESULT_OK
            && requestCode == BOOK_SPOT_REQUEST_CODE){
            getPosts(mUser.city)
        }else if(resultCode == Activity.RESULT_OK &&
                requestCode == APPROVE_SPOT_REQUEST_CODE){
            getPosts(mAdmin.city)
        }else if (resultCode == Activity.RESULT_OK
            && requestCode == FREE_SHOP_REQUEST_CODE
        ) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().loadUserData(this@MainActivity)
        }
        else {
            Log.e("Cancelled", "Cancelled")
        }
    }

    companion object {
        //A unique code for starting the activity for result
        const val EDIT_PROFILE_REQUEST_CODE: Int = 11
        const val CREATE_POST_REQUEST_CODE: Int = 12
        const val BOOK_SPOT_REQUEST_CODE : Int = 13
        const val APPROVE_SPOT_REQUEST_CODE : Int = 14
        const val FREE_SHOP_REQUEST_CODE : Int = 15
    }
}