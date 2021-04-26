package com.example.evergreen.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.evergreen.R
import com.example.evergreen.activities.BookedSpotsActivity
import com.example.evergreen.activities.UploadImageAfterActivity
import com.example.evergreen.firebase.FirestoreClass
import com.example.evergreen.model.Post
import com.example.evergreen.utils.Constants
import kotlinx.android.synthetic.main.item_booked_spot.view.*

class BookedSpotsAdapter(private val context: Context,
                         private var list: ArrayList<Post>,
                         private var creatorsList : ArrayList<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


//    private var onClickListener: OnClickListener? = null

    lateinit var mod : Post

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.item_booked_spot,
                        parent,
                        false
                )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        mod = list[position]

        if (holder is MyViewHolder) {
            Glide
                .with(context)
                .load(model.imageBefore)
                .centerCrop()
                .placeholder(R.drawable.ic_post_image_150)
                .into(holder.itemView.iv_post_image_booked_spot)


            holder.itemView.tv_location_booked_spot.text = model.location
            if(model.descriptionByCreator.isNotEmpty())
            holder.itemView.tv_description_booked_spot.text = model.descriptionByCreator
            else holder.itemView.tv_description_booked_spot.text = Constants.NO_DESCRIPTION_AVAILABLE

            holder.itemView.tv_posted_by_booked_spot.text = creatorsList[position]
            holder.itemView.tag = model

//            holder.itemView.setOnClickListener {
//                if (onClickListener != null) {
//                    onClickListener!!.onClick(position, model)
//                }
//            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder : RecyclerView.ViewHolder, View.OnClickListener{
        var btnUpload :View = itemView.findViewById<View>(R.id.btn_upload_image_after) as View
        var btnUnbook : View = itemView.findViewById<View>(R.id.btn_unbook_spot) as View
        var context : Context  = itemView.context

        constructor(itemView: View) : super(itemView) {
            btnUpload.setOnClickListener(this)
            btnUnbook.setOnClickListener(this)

        }

        override fun onClick(v: View) {
            val post= itemView.tag as Post
            when(v.id){
                R.id.btn_upload_image_after ->{
                    val intent = Intent(v.context, UploadImageAfterActivity::class.java)
                    intent.putExtra(Constants.POST_DETAIL, post)
                    v.context.startActivity(intent)
                }
                R.id.btn_unbook_spot ->{
                    var activity = context as Activity
                    //activity.showProgressDialog()
                    Log.i("unbook","${activity.toString()}")
                   FirestoreClass().unBookSpot(activity,post)
                }
            }
        }
    }

}