package com.example.evergreen.activities

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.example.evergreen.R
import com.example.evergreen.firebase.FirebaseAuthClass
import com.example.evergreen.firebase.FirestoreClass
import com.example.evergreen.model.Post
import com.example.evergreen.model.User
import com.example.evergreen.utils.Constants
import com.google.android.gms.location.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.io.File
import java.io.IOException
import java.util.*

class CreatePostActivity : BaseActivity(), View.OnClickListener{

    private lateinit var mUser : User

    private lateinit var selectedImage : Bitmap
    lateinit var mCurrentPhotoPath: String
    private var mLatitude: Double = 0.0 // A variable which will hold the latitude value. will be used in places api
    private var mLongitude: Double = 0.0 // A variable which will hold the longitude value.
    private var mCity : String = ""
    private var mImageBeforeURL :String = ""
    // Add a global variable for URI of a selected image from phone storage.
    private var mSelectedImageFileUri: Uri? = null

    // A global variable for user details.
    private var mPostDetails: Post = Post()

    // A fused location client variable which is further user to get the user's current location
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        // Initialize the Fused location variable
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        /**
         * Initialize the places sdk if it is not initialized earlier using the api key.
         */
//        if (!Places.isInitialized()) {
//            Places.initialize(
//                this@CreatePostActivity,
//                resources.getString(R.string.google_maps_api_key)
//            )
//        }
        if(intent.hasExtra(Constants.USER_DETAIL)){
            mUser = intent.getParcelableExtra<User>(Constants.USER_DETAIL)!!
        }
        setupActionBar()
        tv_add_image.setOnClickListener(this)
        et_location.setOnClickListener(this)
        tv_select_current_location_createPost.setOnClickListener(this)
        btn_save.setOnClickListener(this)
//        later on for places api, edit location
    }

    /**
     * A function to setup action bar
     */
    private fun setupActionBar() {

        setSupportActionBar(toolbar_create_post_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.create_post)
        }

        toolbar_create_post_activity.setNavigationOnClickListener { onBackPressed() }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_add_image -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
                pictureDialog.setItems(pictureDialogItems){ dialog, which ->
                    when (which) {
                        // Here we have createD the methods for image selection from GALLERY
                        0 -> choosePhotoFromGallery()
                        1 -> dispatchTakePictureIntent()//takePhotoFromCamera()
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

            R.id.tv_select_current_location_createPost -> {
                selectCurrentLocation(this)
                mCity = getCityFromBase()
                mPostDetails.city = mCity
            }

            R.id.btn_save -> {
                when {
                    et_location.text.isNullOrEmpty() -> {
                        showErrorSnackBar("Please enter a location!!")
                    }
                    mSelectedImageFileUri == null -> {
                        showErrorSnackBar("Please choose an image!!")
                    }
                    else -> {
                        uploadPostImageBefore()
                    }
                }
            }
        }
    }

    private fun fillPostDetails() {
        mPostDetails.imageBefore = mImageBeforeURL
        Log.i("postcreate", " my post url is ${mPostDetails.imageBefore}")

        if(mCity.isEmpty()) {
            setLatLangFromAddress(et_location.text.toString())
            mCity = getCityFromBase()
        }

        mPostDetails.state = getStateFromBase()
        mPostDetails.location = et_location.text.toString()
        mPostDetails.city = mCity
        mPostDetails.postedBy = FirebaseAuthClass().getCurrentUserID()
        mPostDetails.status = Constants.SPOT_UNDER_REVIEW
        mPostDetails.descriptionByCreator = et_description_create_post.text.toString()

        Log.e("postcreate", mPostDetails.toString())

        FirestoreClass().createPost(this,mPostDetails)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = absolutePath
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun dispatchTakePictureIntent() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                                 Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        // Here after all the permission are granted launch the gallery to select an image.
                        if (report!!.areAllPermissionsGranted()) {
                            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                                // Ensure that there's a camera activity to handle the intent
                                takePictureIntent.resolveActivity(packageManager)?.also {
                                    // Create the File where the photo should go
                                    val photoFile: File? = try {
                                        createImageFile()
                                    } catch (ex: IOException) {
                                        Log.e("camera",ex.message!!)
                                        // Error occurred while creating the File
                                        null
                                    }
                                    // Continue only if the File was successfully created
                                    photoFile?.also {
                                        val photoURI: Uri = FileProvider.getUriForFile(this@CreatePostActivity, "com.example.evergreen.fileprovider", it)
                                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                                        startActivityForResult(takePictureIntent, CAMERA)
                                    }
                                }
                            }
                        }else{
                            showRationalDialogForPermissions()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?)
                    {
                        showRationalDialogForPermissions()
                    }
                }).onSameThread().check()
    }

    private fun choosePhotoFromGallery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        // Here after all the permission are granted launch the gallery to select an image.
                        if (report!!.areAllPermissionsGranted()) {
                            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            startActivityForResult(galleryIntent, GALLERY)
                        }else{
                            showRationalDialogForPermissions()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?)
                    {
                        showRationalDialogForPermissions()
                    }
                }).onSameThread().check()
    }

    private fun uploadPostImageBefore() {

        showProgressDialog(resources.getString(R.string.please_wait))

        if (mSelectedImageFileUri != null) {

            //getting the storage reference
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                    "POST_IMAGE_BEFORE" + System.currentTimeMillis() + "."
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
                                    mImageBeforeURL = uri.toString()
                                    fillPostDetails()
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
                        iv_place_image!!.setImageBitmap(selectedImageBitmap) // Set the selected image from GALLERY to imageView.
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this@CreatePostActivity, "Please try again !!", Toast.LENGTH_SHORT).show()
                    }
                }
            }else if (requestCode == CAMERA) {
                val filePath = File(mCurrentPhotoPath)
                iv_place_image.setImageURI(Uri.fromFile(filePath))
                mSelectedImageFileUri = Uri.fromFile(filePath)

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

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton("GO TO SETTINGS") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

     fun onPostCreatedSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        Toast.makeText(this, "Post Created Successfully! Other Users will be able to see it in their feed" +
                " after your post gets approved!!", Toast.LENGTH_LONG).show()
        finish()
    }

    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
        // A constant variable for place picker
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 3
    }
}