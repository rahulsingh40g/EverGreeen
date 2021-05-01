
package com.example.evergreen.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.evergreen.R
import com.example.evergreen.firebase.FirebaseAuthClass
import com.example.evergreen.firebase.FirestoreClass
import com.example.evergreen.model.Feedback
import kotlinx.android.synthetic.main.activity_feedback.*

class FeedbackActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        setupActionBar()

        btn_submit_feedback.setOnClickListener{
            if(et_feedback.text.toString().isNotEmpty()){
                showProgressDialog(resources.getString(R.string.please_wait))
                var feedback = Feedback(FirebaseAuthClass().getCurrentUserID(),
                        FirebaseAuthClass().getCurrentUserMailId(), et_feedback.text.toString() )
                FirestoreClass().submitFeedback(this, feedback)
            }else showErrorSnackBar("Please enter some feedback or query.")
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_feedback_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.feedback_queries)
        }

        toolbar_feedback_activity.setNavigationOnClickListener { onBackPressed() }
    }

    fun feedbackSuccess(){
        hideProgressDialog()
        Toast.makeText(this, "Feedback / Query submitted successfully.", Toast.LENGTH_SHORT).show()
        finish()
    }
}