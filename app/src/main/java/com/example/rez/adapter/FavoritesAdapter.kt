package com.example.rez.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.databinding.FavoritesLayoutBinding
import com.example.rez.databinding.SuggestionAndNearLayoutBinding
import com.example.rez.model.authentication.response.Favourite
import com.example.rez.ui.GlideApp
import com.example.rez.util.visible

class FavoritesAdapter(private var favList: List<Favourite>, val onFavoritesItemClickListener: OnClickFavoritesItemClickListener): RecyclerView.Adapter<FavoritesAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding: FavoritesLayoutBinding): RecyclerView.ViewHolder(binding.root) {

        val hotelImageIv = binding.hotelImageIv
        val like = binding.likeIv
        val tableQtyTv = binding.tableQtyTv
        val categoryTv = binding.categoryTv
        val hotelNameTv = binding.hotelNameTv
        val addressTv = binding.addressTv
        val rating = binding.ratingBar
        val distanceTv = binding.distanceTv
        // val cardView = binding.cardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = FavoritesLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.apply {
            with(holder){
                with(favList[position]){
                    GlideApp.with(context).load(avatar).into(hotelImageIv)
                    hotelNameTv.text = company_name
                    rating.rating = ratings
                }
            }
        }
        holder.itemView.setOnClickListener {
            onFavoritesItemClickListener.onEachFavoriteItemClickListener(favList[position])
        }
        holder.like.setOnClickListener {
            onFavoritesItemClickListener.unLikeFavoritesItem(favList[position].id.toString(), like = holder.like)
        }
    }

    override fun getItemCount(): Int {
        return favList.size
    }

    interface OnClickFavoritesItemClickListener {
        fun onEachFavoriteItemClickListener(favorites: Favourite)
        fun unLikeFavoritesItem(id: String, like: ImageView)
    }
}

