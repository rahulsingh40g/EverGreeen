package com.example.evergreen.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.evergreen.activities.*
import com.example.evergreen.model.*
import com.example.evergreen.utils.Constants
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity : SignUpActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS)
            // Document ID for users fields. Here the document it is the User ID.
            .document(userInfo.uid)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                // Here call a function of base activity for transferring the result to it.
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document",
                    e
                )
            }
    }

    fun loadUserData(activity: Activity) {

        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.USERS)
            // The document id to get the Fields of user.
            .document(FirebaseAuthClass().getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())

                // Here we have received the document snapshot which is converted into the User Data model object.
                val loggedInUser = document.toObject(User::class.java)!!

                // Here call a function of base activity for transferring the result to it.
//                activity.signInSuccess(loggedInUser)

                 when (activity) {
                    is SignInActivity -> {
                        activity.signInSuccessUser(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                    is EditProfileActivity -> {
                        activity.setUserDataInUI(loggedInUser)
                    }
                     is SplashActivity ->{
                         activity.signInSuccessUser(loggedInUser)
                     }
                     is PlantedMyMeActivity ->{
                         val myPostsList : ArrayList<String> = loggedInUser.bookedPostIds
                         for(post in myPostsList){
                             Log.i("myPosts","${post.toString()}")
                         }
                         getPostFromIdArray(myPostsList,activity,Constants.SPOT_PLANTED)
                     }
                     is BookedSpotsActivity ->{
                         val myPostsList : ArrayList<String> = loggedInUser.bookedPostIds
                         for(post in myPostsList){
                             Log.i("myPosts","${post.toString()}")
                         }
                         getPostFromIdArray(myPostsList,activity,Constants.SPOT_BOOKED)
                     }
                }
                
            }
            .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.
//                  activity.hideProgressDialog()
                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    is EditProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                    is SplashActivity ->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting loggedIn user details",
                    e
                )
            }
    }

    //remove from booked
    private fun updateUserBookedPostIds(activity: Activity,postId :String) {
        mFireStore.collection(Constants.USERS)
                .document(FirebaseAuthClass().getCurrentUserID())
                .update(Constants.BOOKED_POST_IDS,FieldValue.arrayRemove(postId))
                .addOnSuccessListener {
                    if(activity is BookedSpotsActivity) activity.unBookSpotSuccess()
                }
                .addOnFailureListener { e ->
                    if(activity is BookedSpotsActivity) activity.hideProgressDialog()
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error in updating booked post Ids.",
                            e
                    )
                }
    }

    private fun getPostFromIdArray(idArray : ArrayList<String>, activity : Activity,statusValue : String){

        if(idArray.isEmpty()){
            if(activity is PlantedMyMeActivity){
                activity.populateRV(ArrayList())
            }
            if(activity is BookedSpotsActivity){
                getNameFromUids(activity,ArrayList())
            }
        }
        else
        mFireStore.collection(Constants.POSTS)
            .whereIn(
                Constants.POSTID,
                idArray
            )
                .whereEqualTo(Constants.STATUS, statusValue)
            .get()
            .addOnSuccessListener { posts ->
                //Log.e(activity.javaClass.simpleName, document.documents.toString())

                var postsList : ArrayList<Post> = ArrayList()
                for (post in posts) {
                    // Convert all the document snapshot to the object using the data model class.
                    val curPost = post.toObject(Post::class.java)!!
                    postsList.add(curPost)
                    Log.i("myPosts","${curPost.toString()}")
                }
                if(activity is PlantedMyMeActivity){
                    activity.populateRV(postsList)
                }
                if(activity is BookedSpotsActivity){
                    getNameFromUids(activity,postsList)
                }

            }
            .addOnFailureListener { e ->
                if(activity is PlantedMyMeActivity){
                    activity.hideProgressDialog()
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    e
                )
            }
    }

    /**
     * A function to update the user profile data into the database.
     */
    fun updateUserProfileData(activity: EditProfileActivity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS) // Collection Name
            .document(FirebaseAuthClass().getCurrentUserID()) // Document ID
            .update(userHashMap) // A hashmap of fields which are to be updated.
            .addOnSuccessListener {
                // Profile data is updated successfully.
                Log.i(activity.javaClass.simpleName, "Profile Data updated successfully!")
                Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                // Notify the success result.
                activity.profileUpdateSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    e
                )
            }
    }

    fun createPost(activity : CreatePostActivity, post: Post) {
        mFireStore.collection(Constants.POSTS)
                .add(post)
                .addOnSuccessListener { documentReference ->
                    //auto generated ID
                    val myPostId = documentReference.id
                    Log.i("postfire", "DocumentSnapshot written with ID: ${myPostId}")
                    val currentUserId = FirebaseAuthClass().getCurrentUserID()

                mFireStore.collection(Constants.POSTS)
                        .document(myPostId)
                        .update(Constants.POSTID, myPostId)
                        .addOnSuccessListener {
                            Log.i("postfire","post added successfully in post database with postid + ${it}")
                        }
                        .addOnFailureListener{  e->
                            Log.e("postfire", e.message!!)
                        }

                mFireStore.collection(Constants.USERS)
                        .document(currentUserId)
                        .update(Constants.MYPOSTIDS,FieldValue.arrayUnion(myPostId))
                        .addOnSuccessListener {
                            Log.i("postfire","post added successfully in user database + ${it}")
                        }
                        .addOnFailureListener{  e->
                            Log.e("postfire", e.message!!)
                        }
                activity.onPostCreatedSuccess()
            }
            .addOnFailureListener { e->
                Log.w("postfire", "Error adding document", e)
                activity.hideProgressDialog()
                activity.showErrorSnackBar("Tackled some error while posting, Please try again!!")
            }
    }

    fun getPostsFromLocality(activity: Activity, locality: String, isState : Boolean, status : String): ArrayList<Post> {
        val posts = ArrayList<Post>()
        val attr = if(isState) Constants.STATE
                    else Constants.CITY

        when(activity){
            is MainActivity ->{
                mFireStore.collection(Constants.POSTS)
                    .whereEqualTo(attr , locality)
                    .whereEqualTo(Constants.STATUS , status)
                    .get()
                    .addOnSuccessListener { it ->
                        for(eachPost in it){
                            val post = eachPost.toObject(Post::class.java)
                            Log.i("posts", "post is ${post.toString()}")
                            posts.add(post)
                        }
                        Log.i("posts",posts.toString())
                        getNameFromUids(activity, posts)
                    }
                    .addOnFailureListener{
                        Log.e("posts",it.message!!)
                        getNameFromUids(activity, posts)
                    }
            }
        }
        return posts
    }

    private fun getNameFromUids(activity: Activity, posts : ArrayList<Post>){
        val creators = ArrayList<String>()
        if(posts.isEmpty()){
            when(activity){
                is MainActivity -> {
                    activity.displayPostsInUI(posts, creators)
                }
                is BookedSpotsActivity ->{
                    activity.populateRV(posts,creators)
                }
            }
        }
        else
        for(post in posts){
            mFireStore.collection(Constants.USERS)
                    .whereEqualTo(Constants.UID, post.postedBy)
                    .get()
                    .addOnSuccessListener { users ->
                        for (user in users){
                            Log.i("posts","user is ${user.toObject(User::class.java).name}")
                            creators.add(user.toObject(User::class.java).name)
                            if(creators.size == posts.size){
                                when(activity){
                                    is MainActivity -> {
                                        activity.displayPostsInUI(posts, creators)
                                    }
                                    is BookedSpotsActivity ->{
                                        activity.populateRV(posts,creators)
                                    }
                                }
                            }
                        }
                    }
                    .addOnFailureListener{
                        Log.e("posts","error in getting user + ${it.message!!}")
                        when(activity){
                            is MainActivity -> {
                                activity.displayPostsInUI(posts, creators)
                            }
                            is BookedSpotsActivity ->{
                                activity.populateRV(posts,creators)
                            }
                        }
                    }
        }
    }

    fun updatePostDetails(activity: Activity, mPostDetails: Post, byAdmin : Boolean = false) {
        mFireStore.collection(Constants.POSTS)
            .document(mPostDetails.postId)
            .set(mPostDetails) //over write the old post
            .addOnSuccessListener {
                Log.i("update", "post updated successfully")
                when (activity) {
                    is BookCumApproveSpotActivity -> {
                        Log.i("postfire" , " admi is $byAdmin")
                        if (!byAdmin) {
                            mFireStore.collection(Constants.USERS)
                                .document(mPostDetails.bookedBy)
                                .update(
                                    Constants.BOOKED_POST_IDS,
                                    FieldValue.arrayUnion(mPostDetails.postId)
                                )
                                .addOnSuccessListener {
                                    Log.i(
                                        "postfire",
                                        "post added successfully in user database after booking + ${it}"
                                    )
                                    activity.onUpdateSuccess()
                                }
                                .addOnFailureListener { e ->
                                    activity.hideProgressDialog()
                                    Log.e("postfire", e.message!!)
                                }
                        } else {
                            activity.onUpdateSuccess()
                        }
                    }
                    is UploadImageAfterActivity -> activity.uploadImageSuccess()
                }
            }
            .addOnFailureListener {
                when(activity){
                    is BookCumApproveSpotActivity -> activity.hideProgressDialog()
                    is UploadImageAfterActivity -> activity.hideProgressDialog()
                }
                Log.e("update", it.message!!)
            }
    }

    //renamed the fun from approve to Approve
    fun getApprovedPosts(activity: ApprovalStatusActivity){
        var postList : ArrayList<Post> = ArrayList()
        Log.i("1posts","hwy")
        mFireStore.collection(Constants.POSTS)
                .whereEqualTo(Constants.POSTED_BY,FirebaseAuthClass().getCurrentUserID())
                // TODO: 29-04-2021 it should be just open for booking, bcz rest we are showing at other places, and so much posts will be in approved
                .whereIn(Constants.STATUS, listOf(Constants.SPOT_OPEN_FOR_BOOKING, Constants.SPOT_BOOKED, Constants.SPOT_PLANTED))
                .get()
                .addOnSuccessListener { posts ->
                    for (post in posts){
                        Log.i("1posts","${post.toObject(Post::class.java)}")
                        postList.add(post.toObject(Post::class.java))
                    }
                    activity.populateRV(postList)
                }
                .addOnFailureListener{
                    Log.e("1posts","error in getting post + ${it.message!!}")
                }
    }

    fun getPostsFromStatusValue(statusValue : String,activity: Activity){
        var postList : ArrayList<Post> = ArrayList()
        mFireStore.collection(Constants.POSTS)
                .whereEqualTo(Constants.STATUS, statusValue)
                .whereEqualTo(Constants.POSTED_BY,FirebaseAuthClass().getCurrentUserID())
                    .get()
                    .addOnSuccessListener { posts ->
                        for (post in posts){
                            Log.i("1posts","${post.toObject(Post::class.java)}")
                            postList.add(post.toObject(Post::class.java))
                        }
                        when(activity){
                            is ApprovalStatusActivity -> {
                                activity.populateRV(postList)
                            }
                            is PlantedStatusActivity->{
                                if(statusValue == Constants.SPOT_PLANTED){
                                    activity.populateRvPlanted(postList)
                                }
                                else{
                                    activity.populateRV(postList)
                                }
                            }
                        }
                    }
                    .addOnFailureListener{
                        when(activity){
                            is ApprovalStatusActivity -> {
                                activity.hideProgressDialog()
                            }
                            is PlantedStatusActivity->{
                                activity.hideProgressDialog()
                            }
                        }
                        Log.e("1posts","error in getting post + ${it.message!!}")
                    }
    }

    fun unBookSpot(activity: Activity,post : Post){
        if(activity is BookedSpotsActivity)  activity.showProgressDialog("Please Wait...")

        mFireStore.collection(Constants.POSTS)
                .document(post.postId)
                .update(Constants.STATUS,Constants.SPOT_OPEN_FOR_BOOKING)
                .addOnSuccessListener {
                    Log.i("unbook","unbooked")
                    removePostId(activity,post.postId)
                }
                .addOnFailureListener { e ->

                    if(activity is BookedSpotsActivity) activity.hideProgressDialog()
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while creating a board.",
                            e
                    )
                }

    }

    private fun removePostId(activity: Activity,postId : String){

        mFireStore.collection(Constants.USERS)
                .document(FirebaseAuthClass().getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->
                    Log.i(activity.javaClass.simpleName, document.toString())

                    // Here we have received the document snapshot which is converted into the User Data model object.
                    val loggedInUser = document.toObject(User::class.java)!!

                    when (activity) {
                        is BookedSpotsActivity ->{
                            updateUserBookedPostIds(activity,postId)
                        }
                    }

                }
                .addOnFailureListener { e ->
                    // Here call a function of base activity for transferring the result to it.
//                  activity.hideProgressDialog()
                    when (activity) {
                        is BookedSpotsActivity->{
                            activity.hideProgressDialog()
                        }

                    }
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while getting loggedIn user details",
                            e
                    )
                }
    }

    fun loadAdminOrUserData(activity: Activity) {
        mFireStore.collection(Constants.ADMINS)
                // The document id to get the Fields of user.
                .document(FirebaseAuthClass().getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->
                    if(document.exists()){
                        val loggedInAdmin : Admin = document.toObject(Admin::class.java)!!
                        when(activity){
                            is SplashActivity ->{
                                activity.signInSuccessByAdmin(loggedInAdmin)
                            }
                            is SignInActivity ->{
                                activity.signInSuccessByAdmin(loggedInAdmin)
                            }
                        }
                    }else{
                        loadUserData(activity)
                    }

                }
                .addOnFailureListener { e ->
                    // Here call a function of base activity for transferring the result to it.
                 when(activity){
                            is SplashActivity ->{
                                activity.hideProgressDialog()
                            }
                            is SignInActivity ->{
                                activity.hideProgressDialog()                            }
                        }
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while getting loggedIn user or admin details",
                            e
                    )
                }
    }

    fun submitFeedback(activity: FeedbackActivity,feedback: Feedback) {
        mFireStore.collection(Constants.FEEDBACK)
                .add(feedback)
                .addOnSuccessListener {
                    Log.i("feedback", "feedback submitted successfully")
                    activity.feedbackSuccess()
                }
                .addOnFailureListener{
                    activity.hideProgressDialog()
                    Log.e("feedback", it.message!!)
                }
    }

    fun donate(activity: MainActivity, amount: Long, user : User) {
        mFireStore.collection(Constants.DONATION)
                .document(Constants.DONATION_DOC)
                .update(Constants.AMOUNT , FieldValue.increment(amount))
                .addOnSuccessListener {
                    Log.i("donation", "success")
                    mFireStore.collection(Constants.USERS)
                            .document(user.uid)
                            .update(Constants.AMOUNT_DONATED, FieldValue.increment(amount))
                            .addOnSuccessListener {
                                Log.i("donation", "user donation success")
                                activity.donationSuccess()
                            }
                            .addOnFailureListener{
                                Log.e("donation", it.message!!)
                                activity.hideProgressDialog()
                            }
                }
                .addOnFailureListener{
                    Log.e("donation", it.message!!)
                    Toast.makeText(activity, "Something went wrong, please try again later.", Toast.LENGTH_SHORT).show()
                    activity.hideProgressDialog()
                }
    }

    fun getDonationAmount(activity: MainActivity) {
        var amount = ""
        mFireStore.collection(Constants.DONATION)
                .document(Constants.DONATION_DOC)
                .get()
                .addOnSuccessListener {
                    val donation = it.toObject(Donation::class.java)
                    if (donation != null) {
                        amount = donation.amount.toString()
                    }
                    activity.getDonateSuccess(amount)
                }
                .addOnFailureListener{
                    Log.e("donation", it.message!!)
                    activity.hideProgressDialog()
                }
    }

    fun updateUserPlantsData(activity : FreeShopActivity ,userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS) // Collection Name
            .document(FirebaseAuthClass().getCurrentUserID()) // Document ID
            .update(userHashMap) // A hashmap of fields which are to be updated.
            .addOnSuccessListener {
                // Profile data is updated successfully.
                Log.i(activity.javaClass.simpleName, "plants bought database done!")
                // Notify the success result.
                activity.plantsBoughtSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    e
                )
            }
    }
}