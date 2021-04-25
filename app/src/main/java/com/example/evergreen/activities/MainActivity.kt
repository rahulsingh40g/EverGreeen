package com.example.evergreen.activities

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.evergreen.R
import com.example.evergreen.adapters.PostItemsAdapter
import com.example.evergreen.firebase.FirebaseAuthClass
import com.example.evergreen.firebase.FirestoreClass
import com.example.evergreen.model.Post
import com.example.evergreen.model.User
import com.example.evergreen.utils.Constants
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.IOException


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    var isFABOpen =false

    // A global variable for User Name
    private lateinit var mUserName: String
    private var mUser = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBar()
        handleIntent(intent)

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().loadUserData(this@MainActivity)

        // Assign the NavigationView.OnNavigationItemSelectedListener to navigation view.
        nav_view.setNavigationItemSelectedListener(this)

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
            R.id.refresh ->{
                showProgressDialog(resources.getString(R.string.please_wait))
                getPosts(mUser.city)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onNewIntent(intent: Intent) {
        setIntent(intent)
        handleIntent(intent)
        super.onNewIntent(intent)
    }
    private fun handleIntent(intent: Intent) {

        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            //use the query to search your data somehow
            Log.i("search",query!!)
            showProgressDialog(resources.getString(R.string.please_wait))
            getPosts(query!!, true)
        }
    }
    fun getPosts(locality: String, isState : Boolean = false){
        val posts : java.util.ArrayList<Post> = FirestoreClass().getPostsFromLocality(this, locality, isState)
        Log.i("posts", "displaying post before but serial thing + ${posts.size} ")
    }

    fun updatePostDetails(postsList: ArrayList<Post>, creators: ArrayList<String>) {
        hideProgressDialog()
        if (postsList.size > 0) {
            Log.i("posts", "displaying posts for rv  ")
            rv_posts_list.visibility = View.VISIBLE
            tv_no_posts_available.visibility = View.GONE

            rv_posts_list.layoutManager = LinearLayoutManager(this@MainActivity)
            rv_posts_list.setHasFixedSize(true)

            val adapter = PostItemsAdapter(this, postsList, creators)
            rv_posts_list.adapter = adapter
            adapter.setOnClickListener(object : PostItemsAdapter.OnClickListener {
                override fun onClick(position: Int, model: Post, postedByName : String) {
                    val intent = Intent(this@MainActivity, BookSpotActivity::class.java)
                    intent.putExtra(Constants.POST_DETAIL, model)
                    intent.putExtra(Constants.POSTEDBYNAME, postedByName)
                    startActivityForResult(intent, BOOK_SPOT_REQUEST_CODE)
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
                        Intent(
                                this@MainActivity, EditProfileActivity::class.java),
                        EDIT_PROFILE_REQUEST_CODE
                )
            }

            R.id.nav_sign_out -> {
                // Here sign outs the user from firebase in this device.
                if (FirebaseAuthClass().getCurrentUserID().isNotEmpty())
                // need to add one alert dialog
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
        } else if (resultCode == Activity.RESULT_OK
            && requestCode == CREATE_POST_REQUEST_CODE
        ) {
            // Get the latest posts list.
                showProgressDialog(resources.getString(R.string.please_wait))
                Log.i("city", "muser city is ${mUser.city}")
                getPosts(mUser.city)
        } else if(resultCode == Activity.RESULT_OK
            && requestCode == BOOK_SPOT_REQUEST_CODE){
            showProgressDialog(resources.getString(R.string.please_wait))
            getPosts(mUser.city)
        }
        else {
            Log.e("Cancelled", "Cancelled")
        }
    }

    fun updateNavigationUserDetails(user: User) {
        mUserName = user.name
        mUser = user

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

    companion object {
        //A unique code for starting the activity for result
        const val EDIT_PROFILE_REQUEST_CODE: Int = 11
        const val CREATE_POST_REQUEST_CODE: Int = 12
        const val BOOK_SPOT_REQUEST_CODE : Int = 13
    }

}