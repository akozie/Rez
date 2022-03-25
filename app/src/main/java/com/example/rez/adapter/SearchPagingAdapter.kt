package com.example.rez.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rez.R
import com.example.rez.databinding.FavoritesLayoutBinding
import com.example.rez.databinding.ReservationListAdapterBinding
import com.example.rez.databinding.SuggestionAdapterBinding
import com.example.rez.model.authentication.response.Booking
import com.example.rez.model.authentication.response.Favourite
import com.example.rez.model.authentication.response.ResultX
import com.example.rez.ui.GlideApp
import com.example.rez.ui.fragment.dashboard.Favorites
import com.example.rez.util.visible

class SearchPagingAdapter(private val onClickListener : OnSearchClickListener): PagingDataAdapter<ResultX, SearchPagingAdapter.BookingMyViewHolder>(differCallback)  {


    inner  class BookingMyViewHolder(private val binding: SuggestionAdapterBinding): RecyclerView.ViewHolder(binding.root) {
        var hotelImage = binding.hotelImageIv
        var hotelName = binding.hotelNameTv
        var hotelRatingBar = binding.ratingBar
        // var hotelReviews = binding.totalReviewTv

    }

    companion object{
        private val differCallback = object : DiffUtil.ItemCallback<ResultX>(){
            override fun areItemsTheSame(oldItem: ResultX, newItem: ResultX): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ResultX, newItem: ResultX): Boolean {
                return oldItem == newItem
            }
        }
    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingMyViewHolder {
            val binding = SuggestionAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return BookingMyViewHolder(binding)
        }

        override fun onBindViewHolder(holder: BookingMyViewHolder, position: Int) {
            val current = getItem(position)
                holder.itemView.apply {
                    with(current){
                        holder.hotelName.text = current?.company_name
                        holder.hotelRatingBar.rating = current?.ratings?.toFloat()!!
                        Glide.with(context).load(current.avatar).into(holder.hotelImage)
                        // hotelImage.setImageResource(restaurantImage!!)
                    }
                }
            holder.itemView.setOnClickListener {
                if (current != null) {
                    onClickListener.onEachSearchClick(current)
                }
            }
        }

        interface OnSearchClickListener {
            fun onEachSearchClick(resultX: ResultX)
        }
}







