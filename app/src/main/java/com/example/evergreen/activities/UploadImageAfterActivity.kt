package com.example.evergreen.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.evergreen.R
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.activity_upload_image_after.*

class UploadImageAfterActivity : BaseActivity(), View.OnClickListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_image_after)

        setupActionBar()
        tv_add_image.setOnClickListener(this)
        et_location.setOnClickListener(this)
        tv_select_current_location_createPost.setOnClickListener(this)
        btn_save.setOnClickListener(this)
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_upload_image_after_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.create_post)
        }

        toolbar_upload_image_after_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
}