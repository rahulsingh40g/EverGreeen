package com.example.evergreen.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.evergreen.R
import com.example.evergreen.firebase.FirestoreClass
import com.example.evergreen.model.User
import com.example.evergreen.utils.Constants
import kotlinx.android.synthetic.main.activity_free_shop.*


class FreeShopActivity : BaseActivity() {
    private lateinit var mUser : User
    var plants : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_free_shop)
        if(intent.hasExtra(Constants.USER_DETAIL))
            mUser = intent.getParcelableExtra<User>(Constants.USER_DETAIL)!!
        setupActionBar()
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_free_shop)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.free_shop)
        }

        toolbar_free_shop.setNavigationOnClickListener { onBackPressed() }
    }

    fun onPlantClicked(view: View) {

        val min = 1
        val max = (mUser.bookedPostIds.size + 5) - mUser.plantsBought
        Log.i("shop", "max is $max")
        val li = LayoutInflater.from(this)
        val promptsView: View = li.inflate(R.layout.alert_dialog_shop, null)
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(
            this, R.style.CustomAlertDialog
        )

        // set alert_dialog.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView)
        val userInput = promptsView.findViewById<View>(R.id.etUserInputPlants) as EditText
        userInput.hint = "$min - $max"

        // set dialog message
        alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton(
                "Order",
                DialogInterface.OnClickListener { dialog, id -> // get user input and set it to result
                    // edit text
                    if (userInput != null) {
                        val input = userInput.text.toString()
                        if (input.isNotEmpty()) {
                            Log.i("shop", "$input")
                            plants = input.toInt()
                            Log.i("shop", " plants are $plants")
                            if (plants < min || plants > max) {
                                showErrorSnackBar(
                                    "We can't give you these much number of plants, " +
                                            "Kindly post your queries in feedback section."
                                )
                            } else {
                                val userHashMap = HashMap<String, Any>()
                                userHashMap[Constants.PLANTS_BOUGHT] = mUser.plantsBought + plants
                                showProgressDialog(resources.getString(R.string.please_wait))
                                FirestoreClass().updateUserPlantsData(this, userHashMap)
                            }
                        } else {
                            showErrorSnackBar("Please enter some number of plants")
                        }
                    } else {
                        Log.e("shop", "null")
                    }
                })
            .setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })


        // create alert dialog
        val alertDialog: AlertDialog = alertDialogBuilder.create()

        // show it
        alertDialog.show()
    }

    fun plantsBoughtSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        Toast.makeText(this, "You have successfully bought $plants plants.", Toast.LENGTH_LONG).show()
        finish()
    }
}