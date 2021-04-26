package com.example.evergreen.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.evergreen.R
import com.example.evergreen.model.Post
import com.example.evergreen.utils.Constants
import kotlinx.android.synthetic.main.item_post.view.*
import kotlinx.android.synthetic.main.item_post.view.tv_location
import kotlinx.android.synthetic.main.item_post_approved.view.*

class ApprovedPostsAdapter(private val context: Context,
                           private var list: ArrayList<Post>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_post_approved,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            Glide
                .with(context)
                .load(model.imageBefore)
                .centerCrop()
                .placeholder(R.drawable.ic_post_image_150)
                .into(holder.itemView.iv_post_image_before_approved)


            holder.itemView.tv_location_approved.text = model.location

            if(model.descriptionByCreator.isNotEmpty())
            holder.itemView.tv_description_approved.text = model.descriptionByCreator
            else
                holder.itemView.tv_description_approved.text = Constants.NO_DESCRIPTION_AVAILABLE
//            holder.itemView.setOnClickListener {
//
//                if (onClickListener != null) {
//                    onClickListener!!.onClick(position, model)
//                }
//            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}