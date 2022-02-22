package com.example.rez.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.databinding.ReviewShowAdapterLayoutBinding
import com.example.rez.model.dashboard.ReviewX
import com.example.rez.ui.GlideApp

class ReviewAdapter(private var reviewList: List<ReviewX>): RecyclerView.Adapter<ReviewAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding: ReviewShowAdapterLayoutBinding): RecyclerView.ViewHolder(binding.root) {

        val userImage = binding.userImageIv
        val userName = binding.userNameTv
        val reviewMessageTv = binding.reviewMessageTv
        val dateTv = binding.dateTv
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ReviewShowAdapterLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.apply {
            with(holder){
                with(reviewList[position]){
                    GlideApp.with(context).load(user.avatar).into(userImage)
                    userName.text = user.first_name + " " + user.last_name
                    reviewMessageTv.text = review
                   // binding.timeTv.text = args?.booked_for?.substring(10)
                    dateTv.text = created_at.substring(0, 10)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

}

