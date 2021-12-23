package com.example.rez.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.databinding.NearRestaurantAdapterBinding
import com.example.rez.model.dashboard.NearRestaurantData


class NearRestaurantAdapter(private var nearRestaurantList: ArrayList<NearRestaurantData>): RecyclerView.Adapter<NearRestaurantAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding:NearRestaurantAdapterBinding): RecyclerView.ViewHolder(binding.root) {
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
        val binding = NearRestaurantAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.apply {
            with(holder){
                with(nearRestaurantList[position]){
                    hotelImageIv.setImageResource(restaurantImage!!)
                    tableQtyTv.text = tables
                    categoryTv.text = categoryName
                    hotelNameTv.text = restaurantName
                    addressTv.text = address
                    distanceTv.text = distance
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return nearRestaurantList.size
    }

}

