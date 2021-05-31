package com.example.evergreen.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.evergreen.R
import com.example.evergreen.firebase.FirebaseAuthClass
import com.example.evergreen.utils.GetAddressFromLatLng
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.dialog_progress.*
import java.io.File
import java.io.IOException
import java.util.*


open class BaseActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false
    private lateinit var mFusedLocationClient: FusedLocationProviderClient // A fused location client variable which is further user to get the user's current location
    private var mLatitude: Double = 0.0 // A variable which will hold the latitude value.
    private var mLongitude: Double = 0.0 // A variable which will hold the longitude value.
    lateinit var currentActivity :Activity
    lateinit var mCurrentPhotoPath: String

    /**
     * This is a progress dialog instance which we will initialize later on.
     */
    private lateinit var mProgressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }



    fun showAlertDialog(activity: Activity, text: String)  {
        AlertDialog.Builder(this)
            .setMessage(text)
            .setTitle("Are you Sure?")
            .setPositiveButton(
                "YES"
            ) { _, _ ->
                when(activity){
                    is BookCumApproveSpotActivity ->{
                        activity.onYesAlert()
                    }
                    is MainActivity ->{
                        if (FirebaseAuthClass().getCurrentUserID().isNotEmpty())
                            FirebaseAuthClass().signOut(this)

                        val intent = Intent(activity, IntroActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        activity.finish()
                    }
                }
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                when(activity){
                    is BookCumApproveSpotActivity ->{
                        Toast.makeText(this, "No worries, You can take your time to make a decision.", Toast.LENGTH_SHORT).show()
                    }
                    is MainActivity ->{
                        dialog.dismiss()
                    }
                }
            }
            .show()
    }

    /**
     * This function is used to show the progress dialog with the title and message to user.
     */
    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.setCancelable(false)


        mProgressDialog.tv_progress_text.text = text

        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }

    /**
     * This function is used to dismiss the progress dialog if it is visible to user.
     */
    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    fun doubleBackToExit() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, resources.getString(R.string.please_click_back_again_to_exit), Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    fun showErrorSnackBar(message: String) {
        val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this@BaseActivity, R.color.snackbar_error_color))
        snackBar.show()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    fun requestNewLocationData() {

        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            mLatitude = mLastLocation.latitude
            Log.i("Current Latitude", "$mLatitude")
            mLongitude = mLastLocation.longitude
            Log.i("Current Longitude", "$mLongitude")

            //Getting an address from the latitude and longitude.
            val addressTask = GetAddressFromLatLng(currentActivity, mLatitude, mLongitude)

            addressTask.setAddressListener(object : GetAddressFromLatLng.AddressListener {
                override fun onAddressFound(address: String?) {
                    Log.i("Address ::", "" + address)
                    when (currentActivity) {
                        is SignUpActivity -> {
                            et_location_signUp.setText(address)
                        }
                        is EditProfileActivity -> {
                            et_location_editProfile.setText(address) // Address is set to the edittext
                        }
                        is CreatePostActivity -> {
                            et_location.setText(address)
                        }
                    }
                }

                override fun onError() {
                    Log.e("Get Address ::", "Something is wrong...")
                }
            })
            addressTask.getAddress()
        }
    }



    fun selectCurrentLocation(activity: Activity){
        currentActivity = activity
        if (!isLocationEnabled()) {
            Toast.makeText(this, "Your location provider is turned off. Please turn it on.", Toast.LENGTH_SHORT).show()
            // This will redirect you to settings from where you need to turn on the location provider.
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        } else {
            // For Getting current location of user please have a look at below link for better understanding
            // https://www.androdocs.com/kotlin/getting-current-location-latitude-longitude-in-android-using-kotlin.html
            Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            requestNewLocationData()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                        showRationalDialogForPermissions(activity)
                    }
                }).onSameThread().check()
        }
    }

    fun getCityFromBase() : String{
        val gcd = Geocoder(this, Locale.getDefault())
        val addresses: List<Address> = gcd.getFromLocation(mLatitude, mLongitude, 1)
        if(addresses.isNotEmpty()) {
            val city = addresses[0].locality
            val state = addresses[0].adminArea
            if(city.isNullOrEmpty()) return ""
            Log.i("city", city)
            Log.i("state",state)
            return city
        } else {
            Log.e("post","returning empty ")
            return ""
        }
    }
    fun getStateFromBase() : String{
        val gcd = Geocoder(this, Locale.getDefault())
        val addresses: List<Address> = gcd.getFromLocation(mLatitude, mLongitude, 1)
        if(addresses.isNotEmpty()) {
            val city = addresses[0].locality
            val state = addresses[0].adminArea
            if(state.isNullOrEmpty()) return ""
            Log.i("state",state)

            return state
        } else {
            Log.e("post","returning empty ")
            return ""
        }
    }
    fun setLatLangFromAddress(strAddress : String) : Boolean{
        val coder = Geocoder(this);
        val address : List<Address>

        try {
            address = coder.getFromLocationName(strAddress,1);
            if (address == null || address.isEmpty()) {
                return false
            }
            val location : Address = address[0];
            mLatitude =  location.latitude
            mLongitude = location.longitude
            return true

        }catch (ex : IOException) {
            ex.printStackTrace()
            return false
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun dispatchTakePictureIntent(activity: Context) {
        Dexter.withContext(activity)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    Log.i("per", "in check funs")
                    report?.let {
                        if (report.areAllPermissionsGranted()) {
                            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                                // Ensure that there's a camera activity to handle the intent
                                takePictureIntent.resolveActivity(packageManager)?.also {
                                    // Create the File where the photo should go
                                    val photoFile: File? = try {
                                        createImageFile()
                                    } catch (ex: IOException) {
                                        Log.e("camera", ex.message!!)
                                        // Error occurred while creating the File
                                        null
                                    }
                                    // Continue only if the File was successfully created
                                    photoFile?.also {
                                        val photoURI: Uri = FileProvider.getUriForFile(
                                            activity,
                                            "com.example.evergreen.fileprovider",
                                            it
                                        )
                                        takePictureIntent.putExtra(
                                            MediaStore.EXTRA_OUTPUT,
                                            photoURI
                                        )
                                        startActivityForResult(takePictureIntent, CAMERA)
                                    }
                                }
                            }
                        } else {
                            showRationalDialogForPermissions(activity = activity as Activity)
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    Log.i("per", "rational")
                    // Remember to invoke this method when the custom rationale is closed
                    // or just by default if you don't want to use any custom rationale.
                    token?.continuePermissionRequest()
                }
            })
            .withErrorListener {
                Log.i("per", it.name)
            }
            .check()

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


    fun choosePhotoFromGallery(activity: Context) {
        Dexter.withContext(activity)
            .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    // Here after all the permission are granted launch the gallery to select an image.
                    if (report!!.areAllPermissionsGranted()) {
                        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(galleryIntent, GALLERY)
                    }else{
                        showRationalDialogForPermissions(activity as Activity)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?)
                {
                    token?.continuePermissionRequest()
                }
            }).onSameThread().check()
    }

    private fun showRationalDialogForPermissions(activity: Activity) {
        Log.i("per","in rationale ")
        AlertDialog.Builder(activity)
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

    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
        // A constant variable for place picker
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 3
    }

}
