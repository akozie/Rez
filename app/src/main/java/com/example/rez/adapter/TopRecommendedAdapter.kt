package com.example.rez.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.databinding.TopRecommendedAdapterBinding
import com.example.rez.model.dashboard.NearRestaurantData
import com.example.rez.model.dashboard.TopRecommendedData
import com.squareup.picasso.Picasso

class TopRecommendedAdapter( private var topRecommendedList:ArrayList<TopRecommendedData>, var clickListener: OnTopItemClickListener ): RecyclerView.Adapter<TopRecommendedAdapter.MyViewHolder>() {


    inner class MyViewHolder(private val binding: TopRecommendedAdapterBinding): RecyclerView.ViewHolder(binding.root) {
        var hotelImage = binding.hotelImageIv
        var hotelName = binding.hotelNameTv
        //var hotelRatingBar = binding.ratingBar
        var hotelReviews = binding.totalReviewTv

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = TopRecommendedAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.apply {
            with(holder){
                with(topRecommendedList[position]){
                    hotelName.text = restaurantName
                    //hotelRatingBar.rating = java.lang.Float.parseFloat(rating)
                    hotelReviews.text = totalReviews
                   // Picasso.get().load(restaurantImage!!)
                   //      .into(hotelImage)
                    hotelImage.setImageResource(restaurantImage!!)
                }
            }
        }
        holder.itemView.setOnClickListener {
            clickListener.onTopItemClick(topRecommendedList[position])
        }
    }

    override fun getItemCount(): Int {
        return topRecommendedList.size
    }
}

interface OnTopItemClickListener {
    fun onTopItemClick(topModel: TopRecommendedData)
}

