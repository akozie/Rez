package com.example.rez.adapter.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rez.R
import com.example.rez.databinding.SearchAdapterBinding
import com.example.rez.model.authentication.response.ResultX

class SearchPagingAdapter(private val onClickListener : OnSearchClickListener): PagingDataAdapter<ResultX, SearchPagingAdapter.BookingMyViewHolder>(
    differCallback
)  {


    inner  class BookingMyViewHolder(private val binding: SearchAdapterBinding): RecyclerView.ViewHolder(binding.root) {
        var hotelImage = binding.hotelImageIv
        var hotelName = binding.hotelNameTv
        var hotelRatingBar = binding.ratingBar

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
            val binding = SearchAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return BookingMyViewHolder(binding)
        }

        override fun onBindViewHolder(holder: BookingMyViewHolder, position: Int) {
            val current = getItem(position)
                holder.itemView.apply {
                    with(current){
                        holder.hotelName.text = current?.company_name
                        if (current?.ratings == 0){
                            holder.hotelRatingBar.rating = 2.toFloat()
                        } else {
                            holder.hotelRatingBar.rating = current?.ratings?.toFloat()!!
                        }
                        if (current.avatar==null){
                            Glide.with(context).load(R.drawable.table_image).into(holder.hotelImage)
                        }else{
                            Glide.with(context).load(current.avatar).into(holder.hotelImage)
                        }
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







