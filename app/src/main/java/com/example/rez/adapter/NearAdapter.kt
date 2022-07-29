package com.example.rez.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rez.R
import com.example.rez.databinding.NearRestaurantAdapterBinding
import com.example.rez.databinding.SuggestionAdapterBinding
import com.example.rez.databinding.SuggestionAndNearLayoutBinding
import com.example.rez.model.dashboard.*
import com.example.rez.util.visible


class NearAdapter(private var nearByList: List<NearbyVendor>, val onNearClickListener:OnNearItemClickListener): RecyclerView.Adapter<NearAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding: SuggestionAndNearLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        val hotelImageIv = binding.hotelImageIv
        val unLikeIv = binding.unLikeIv
        val likeIv = binding.likeIv
        val tableQtyTv = binding.tableQtyTv
        val categoryTv = binding.categoryTv
        val hotelNameTv = binding.hotelNameTv
//        val addressTv = binding.addressTv
      //  val ratingTv = binding.ratingBar
//        val distanceTv = binding.distanceTv
        // val cardView = binding.cardView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = SuggestionAndNearLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.apply {
            with(holder){
                with(nearByList[position]){
                    if (avatar != null){
                        Glide.with(context).load(avatar).into(hotelImageIv)
                    } else if (avatar == null ){
                        Glide.with(context).load(R.drawable.restaurant).into(hotelImageIv)
                    }
                    hotelNameTv.text = company_name
                    //ratingTv.rating = average_rating
                    categoryTv.text = category_name
                    if (total_tables == 1){
                        tableQtyTv.text = total_tables.toString() + " table"
                    }else{
                        tableQtyTv.text = total_tables.toString() + " tables"
                    }
                    if (liked_by_user){
                        likeIv.visible(true)
                        unLikeIv.visible(false)
                    }else{
                        likeIv.visible(false)
                        unLikeIv.visible(true)
                    }
                }
            }
        }
        holder.itemView.setOnClickListener {
            onNearClickListener.onNearItemClick(nearByList[position])
        }
        holder.likeIv.setOnClickListener {
            onNearClickListener.likeUnlikeNearItem(nearByList[position].id.toString(), holder.likeIv, holder.unLikeIv )
        }
        holder.unLikeIv.setOnClickListener {
            onNearClickListener.likeUnlikeNearItem(nearByList[position].id.toString(), holder.likeIv, holder.unLikeIv)
        }
    }

    override fun getItemCount(): Int {
        return nearByList.size
    }
    interface OnNearItemClickListener {
        fun onNearItemClick(nearbyVendor: NearbyVendor)
        fun likeUnlikeNearItem(id: String, like: ImageView, unlike: ImageView)
    }
}
