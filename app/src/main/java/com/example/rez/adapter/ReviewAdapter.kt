package com.example.rez.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.databinding.ReviewShowAdapterLayoutBinding
import com.example.rez.model.dashboard.ReviewData

class ReviewAdapter(private var reviewList: ArrayList<ReviewData>): RecyclerView.Adapter<ReviewAdapter.MyViewHolder>() {

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
                    userImage.setImageResource(banner_image)
                    userName.text = name
                    reviewMessageTv.text = review
                    dateTv.text = entrydt
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

}

