package com.example.rez.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.R
import com.example.rez.databinding.FavoritesLayoutBinding
import com.example.rez.databinding.ReservationListAdapterBinding
import com.example.rez.model.authentication.response.Booking
import com.example.rez.model.authentication.response.Favourite
import com.example.rez.ui.GlideApp
import com.example.rez.ui.fragment.dashboard.Favorites
import com.example.rez.util.visible

class FavoritesPagingAdapter(private val onFavoritesItemClickListener: OnClickFavoritesItemClickListener): PagingDataAdapter<Favourite, FavoritesPagingAdapter.BookingMyViewHolder>(differCallback)  {


    inner  class BookingMyViewHolder(private val binding: FavoritesLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        val hotelImageIv = binding.hotelImageIv
        val like = binding.likeIv
        val hotelNameTv = binding.hotelNameTv
    }

    companion object{
        private val differCallback = object : DiffUtil.ItemCallback<Favourite>(){
            override fun areItemsTheSame(oldItem: Favourite, newItem: Favourite): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Favourite, newItem: Favourite): Boolean {
                return oldItem == newItem
            }
        }
    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingMyViewHolder {
            val binding = FavoritesLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return BookingMyViewHolder(binding)
        }

        override fun onBindViewHolder(holder: BookingMyViewHolder, position: Int) {
            val current = getItem(position)
                holder.itemView.apply {
                        with(current){
                            GlideApp.with(context).load(current?.avatar).into(holder.hotelImageIv)
                            holder.hotelNameTv.text = current?.company_name
                        }
                }
            holder.itemView.setOnClickListener {
                onFavoritesItemClickListener.onEachFavoriteItemClickListener(current!!)
            }
            holder.like.setOnClickListener {
                onFavoritesItemClickListener.unLikeFavoritesItem(current?.id.toString(), like = holder.like)
            }
                }
    interface OnClickFavoritesItemClickListener {
        fun onEachFavoriteItemClickListener(favorites: Favourite)
        fun unLikeFavoritesItem(id: String, like: ImageView)
    }
}





