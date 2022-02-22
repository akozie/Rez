package com.example.rez.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rez.databinding.SuggestionAdapterBinding
import com.example.rez.model.authentication.response.ResultX
import com.example.rez.model.dashboard.RecommendedVendor

class SearchAdapter( private var searchList:List<ResultX>,  val onClickListener :OnSearchClickListener): RecyclerView.Adapter<SearchAdapter.MyViewHolder>() {


    inner class MyViewHolder(private val binding: SuggestionAdapterBinding): RecyclerView.ViewHolder(binding.root) {
        var hotelImage = binding.hotelImageIv
        var hotelName = binding.hotelNameTv
        var hotelRatingBar = binding.ratingBar
       // var hotelReviews = binding.totalReviewTv

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = SuggestionAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.apply {
            with(holder){
                with(searchList[position]){
                    hotelName.text = company_name
                    hotelRatingBar.rating = ratings.toFloat()
                    Glide.with(context).load(avatar).into(hotelImage)
                   // hotelImage.setImageResource(restaurantImage!!)
                }
            }
        }
        holder.itemView.setOnClickListener {
            onClickListener.onEachSearchClick(searchList[position])
        }
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    interface OnSearchClickListener {
        fun onEachSearchClick(resultX: ResultX)
    }
}


