package com.example.evergreen.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.evergreen.R
import com.example.evergreen.firebase.FirestoreClass
import com.example.evergreen.model.Post
import com.example.evergreen.utils.Constants
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_upload_image_after.*
import java.io.File
import java.io.IOException
import java.util.*

class UploadImageAfterActivity : BaseActivity(), View.OnClickListener{

    private var mSelectedImageFileUri: Uri? = null
    private lateinit var selectedImage : Bitmap
    private var mImageAfterURL :String = ""
    private var mPostDetails: Post = Post()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_image_after)

        setupActionBar()

        if(intent.hasExtra(Constants.POST_DETAIL))
            mPostDetails = intent.getParcelableExtra<Post>(Constants.POST_DETAIL)!!

        tv_add_image_upload_image_after.setOnClickListener(this)
        btn_save_upload_image_after.setOnClickListener(this)

    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_upload_image_after_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = "Plantation"
        }

        toolbar_upload_image_after_activity.setNavigationOnClickListener { onBackPressed() }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_add_image_upload_image_after -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
                pictureDialog.setItems(pictureDialogItems){ dialog, which ->
                    when (which) {
                        // Here we have createD the methods for image selection from GALLERY
                        0 -> choosePhotoFromGallery(this)
                        1 -> dispatchTakePictureIntent(this)//takePhotoFromCamera()
                    }
                }
                pictureDialog.show()
            }
// for future use
//            R.id.et_location_createPost -> {
//                try {
//                    // These are the list of fields which we required is passed
//                    val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
//                    // Start the autocomplete intent with a unique request code.
//                    val intent =Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this@CreatePostActivity)
//                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }

            R.id.btn_save_upload_image_after -> {
                    if(mSelectedImageFileUri == null){
                        showErrorSnackBar("Please choose an image!!")
                    }
                    else{
                        uploadPostImageAfter()
                    }
            }
        }
    }

    private fun uploadPostImageAfter() {
        showProgressDialog(resources.getString(R.string.please_wait))

        if (mSelectedImageFileUri != null) {

            //getting the storage reference
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                    "POST_IMAGE_AFTER" + System.currentTimeMillis() + "."
                            + Constants.getFileExtension(this, mSelectedImageFileUri)
            )

            //adding the file to reference
            sRef.putFile(mSelectedImageFileUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        // The image upload is success
                        Log.e(
                                "Firebase Image URL",
                                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                        )

                        // Get the downloadable url from the task snapshot
                        taskSnapshot.metadata!!.reference!!.downloadUrl
                                .addOnSuccessListener { uri ->
                                    Log.e("Downloadable Image URL", uri.toString())

                                    // assign the image url to the variable.
                                    mImageAfterURL = uri.toString()
                                    mPostDetails.descriptionByPlanter = et_description_upload_image_after.text.toString()
                                    mPostDetails.imageAfter = mImageAfterURL
                                    mPostDetails.status = Constants.SPOT_PLANTED
                                    FirestoreClass().updatePostDetails(this,mPostDetails)
                                }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(
                                this,
                                exception.message,
                                Toast.LENGTH_LONG
                        ).show()

                        hideProgressDialog()
                    }
        }
    }

    fun uploadImageSuccess(){
        hideProgressDialog()
        setResult(RESULT_OK)
        //TODO the intent after this snackbar starts before tha snackbar ends, check it please (onDismissed lgaya h fir bhi nhi hua )
        val snackBar = Snackbar.make(findViewById(android.R.id.content), "You did a great job !!. Keep up the good work :)", Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this@UploadImageAfterActivity, R.color.greenlight))
        snackBar.setCallback(object : Snackbar.Callback() {
            override fun onDismissed(snackbar: Snackbar, event: Int) {
                super.onDismissed(snackbar, event)
                startActivity(Intent(this@UploadImageAfterActivity, PlantedMyMeActivity::class.java))
            }
        })
        snackBar.show()
        finish()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    val contentURI = data.data
                    mSelectedImageFileUri = contentURI

                    try {
                        // could be done with glide too. or // could directly set uri as done with camera below
                        // Here this is used to get an bitmap from URI
                        @Suppress("DEPRECATION")
                        val selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                        selectedImage = selectedImageBitmap
                        iv_place_image_upload_image_after!!.setImageBitmap(selectedImageBitmap) // Set the selected image from GALLERY to imageView.
                        tv_add_image_upload_image_after.text = "Change image"
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this@UploadImageAfterActivity, "Please try again !!", Toast.LENGTH_SHORT).show()
                    }
                }
            }else if (requestCode == CAMERA) {
                val filePath = File(mCurrentPhotoPath)
                iv_place_image_upload_image_after.setImageURI(Uri.fromFile(filePath))
                mSelectedImageFileUri = Uri.fromFile(filePath)
                tv_add_image_upload_image_after.text = "Change image"
                //                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap // Bitmap from camera
//                selectedImage = thumbnail
//                iv_place_image!!.setImageBitmap(thumbnail) // Set to the imageView.
            }
//            else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
//                val place: Place = Autocomplete.getPlaceFromIntent(data!!)
//                et_location_createPost.setText(place.address)
//                mLatitude = place.latLng!!.latitude
//                mLongitude = place.latLng!!.longitude
//            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Cancelled", "Cancelled")
        }
    }

    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
        // A constant variable for place picker
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 3
    }

}


