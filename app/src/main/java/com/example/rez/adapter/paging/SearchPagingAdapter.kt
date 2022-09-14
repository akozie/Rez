package com.example.rez.adapter.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rez.R
import com.example.rez.databinding.SearchAdapterBinding
import com.example.rez.model.authentication.response.ResultX
import com.example.rez.model.dashboard.SuggestedVendor
import com.example.rez.util.visible

class SearchPagingAdapter(private val onClickListener : OnSearchClickListener): PagingDataAdapter<ResultX, SearchPagingAdapter.BookingMyViewHolder>(
    differCallback
)  {


    inner  class BookingMyViewHolder(private val binding: SearchAdapterBinding): RecyclerView.ViewHolder(binding.root) {
        var hotelImage = binding.hotelImageIv
        var hotelName = binding.hotelNameTv
        var hotelRatingBar = binding.ratingBar
//        val unLike = binding.unLikeIv
//        val like = binding.likeIv
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
                        holder.hotelRatingBar.rating = current?.ratings?.toFloat()!!
                        if (current.avatar==null){
                            Glide.with(context).load(R.drawable.table_image).into(holder.hotelImage)
                        }else{
                            Glide.with(context).load(current.avatar).into(holder.hotelImage)
                        }
//                        if (current.liked_by_user){
//                            like.visible(true)
//                            unLike.visible(false)
//                        }else{
//                            like.visible(false)
//                            unLike.visible(true)
//                        }
                        // hotelImage.setImageResource(restaurantImage!!)
                    }
                }
//            holder.unLike.setOnClickListener {
//                onClickListener.likeUnlike(suggestionRestaurantList[position].id.toString(), holder.like, holder.unLike)
//            }
//            holder.like.setOnClickListener {
//                onClickListener.likeUnlike(likeUnlike[position].id.toString(), holder.like, holder.unLike)
//            }
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







