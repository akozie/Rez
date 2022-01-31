package com.example.rez.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.databinding.BannerListAdapterBinding
import com.example.rez.databinding.NearRestaurantAdapterBinding
import com.example.rez.databinding.SuggestionAndNearLayoutBinding
import com.example.rez.model.authentication.response.Favourite
import com.example.rez.model.authentication.response.GetFavoritesResponse
import com.example.rez.model.dashboard.BannerData
import com.example.rez.model.dashboard.NearRestaurantData
import com.example.rez.ui.GlideApp
import com.squareup.picasso.Picasso

class FavoritesAdapter(private var favList: List<Favourite>): RecyclerView.Adapter<FavoritesAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding: SuggestionAndNearLayoutBinding): RecyclerView.ViewHolder(binding.root) {

        val hotelImageIv = binding.hotelImageIv
        val unLikeIv = binding.unLikeIv
        val likeIv = binding.likeIv
        val tableQtyTv = binding.tableQtyTv
        val categoryTv = binding.categoryTv
        val hotelNameTv = binding.hotelNameTv
        val addressTv = binding.addressTv
        val ratingTv = binding.ratingTv
        val distanceTv = binding.distanceTv
        // val cardView = binding.cardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = SuggestionAndNearLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.apply {
            with(holder){
                with(favList[position]){
                    GlideApp.with(context).load(avatar).into(hotelImageIv)
                  //  tableQtyTv.text =
              //      categoryTv.text = categoryName
                    hotelNameTv.text = company_name
//                    addressTv.text = address
//                    distanceTv.text = distance
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return favList.size
    }

}

