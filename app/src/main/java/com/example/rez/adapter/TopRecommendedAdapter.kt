package com.example.rez.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rez.R
import com.example.rez.databinding.TopRecommendedAdapterBinding
import com.example.rez.databinding.TopRecommendedAdapterLayoutBinding
import com.example.rez.model.dashboard.RecommendedVendor

class TopRecommendedAdapter( private var topRecommendedList:List<RecommendedVendor>, var onTopItemClickListener: OnTopItemClickListener ): RecyclerView.Adapter<TopRecommendedAdapter.MyViewHolder>() {


    inner class MyViewHolder(private val binding: TopRecommendedAdapterLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        var hotelImage = binding.hotelImageIv
        var hotelName = binding.hotelNameTv
       // var hotelRatingBar = binding.ratingBar
        //var hotelReviews = binding.totalReviewTv

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = TopRecommendedAdapterLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.apply {
            with(holder){
                with(topRecommendedList[position]){
                    hotelName.text = company_name
                    if (avatar == null){
                        Glide.with(context).load(R.drawable.table_image).into(hotelImage)
                    } else {
                        Glide.with(context).load(avatar).into(hotelImage)
                    }
                }
            }
        }
        holder.itemView.setOnClickListener {
            onTopItemClickListener.onTopItemClick(topRecommendedList[position])
        }
    }

    override fun getItemCount(): Int {
        return topRecommendedList.size
    }
}

interface OnTopItemClickListener {
    fun onTopItemClick(topModel: RecommendedVendor)
}

