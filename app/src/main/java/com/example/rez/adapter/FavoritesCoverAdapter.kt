package com.example.rez.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rez.R
import com.example.rez.databinding.FavoritesCoverRecyclerBinding
import com.example.rez.model.authentication.response.Favourite
import com.example.rez.model.dashboard.Favorite

class FavoritesCoverAdapter(private var tableList: List<Favorite>, val onClickFavoritesCoverItemClickListener: OnClickFavoritesCoverItemClickListener): RecyclerView.Adapter<FavoritesCoverAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding: FavoritesCoverRecyclerBinding): RecyclerView.ViewHolder(binding.root) {

        val favoritesState = binding.favoritesCoverState
        val favoritesStateNumber = binding.favoritesCoverStateNumber
        val favoritesStateImage = binding.fragmentCoverImage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = FavoritesCoverRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.apply {
            with(holder){
                with(tableList[position]){
                    favoritesState.text = state
                    favoritesStateNumber.text = count.toString()
                    if (state == "Abuja"){
                        Glide.with(context).load(R.drawable.abuja).into(favoritesStateImage)
                    } else {
                        Glide.with(context).load(R.drawable.lagos).into(favoritesStateImage)
                    }
                }
            }
        }
        holder.itemView.setOnClickListener {
            onClickFavoritesCoverItemClickListener.onEachFavoriteItemClickListener(tableList[position])
        }
    }

    override fun getItemCount(): Int {
        return tableList.size
    }

    interface OnClickFavoritesCoverItemClickListener {
        fun onEachFavoriteItemClickListener(favorite: Favorite)
    }
}

