package com.example.rez.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rez.R
import com.example.rez.databinding.SuggestionAdapterBinding
import com.example.rez.model.dashboard.SuggestedVendor
import com.example.rez.util.visible


class SuggestionRestaurantAdapter(private var suggestionRestaurantList: List<SuggestedVendor>, val clickListener: OnSuggestionClickListener): RecyclerView.Adapter<SuggestionRestaurantAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding: SuggestionAdapterBinding): RecyclerView.ViewHolder(binding.root) {
        val hotelImageIv = binding.hotelImageIv
        val unLike = binding.unLikeIv
        val like = binding.likeIv
        val tableQty = binding.tableQtyTv
        val category = binding.categoryTv
        val hotelName = binding.hotelNameTv
        val topRating = binding.ratingBar

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = SuggestionAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.apply {
            with(holder){
                with(suggestionRestaurantList[position]){
                    if (avatar == null){
                        Glide.with(context).load(R.drawable.table_image).into(hotelImageIv)
                    } else {
                        Glide.with(context).load(avatar).into(hotelImageIv)
                    }
                    hotelName.text = company_name
                    category.text = category_name
                    topRating.rating = average_rating.toFloat()
                    if (total_tables == 1 || total_tables == 0){
                        tableQty.text = total_tables.toString() + " Table"
                    }else{
                        tableQty.text = total_tables.toString() + " Tables"
                    }
                    if (liked_by_user){
                        like.visible(true)
                        unLike.visible(false)
                    }else{
                        like.visible(false)
                        unLike.visible(true)
                    }
                }
            }
        }
        holder.unLike.setOnClickListener {
            clickListener.likeUnlike(suggestionRestaurantList[position].id.toString(), holder.like, holder.unLike)
        }
        holder.like.setOnClickListener {
            clickListener.likeUnlike(suggestionRestaurantList[position].id.toString(), holder.like, holder.unLike)
        }
        holder.itemView.setOnClickListener {
            clickListener.onSuggestionItemClick(suggestionRestaurantList[position])
        }
    }

    override fun getItemCount(): Int {
        return suggestionRestaurantList.size
    }
}

interface OnSuggestionClickListener {
    fun onSuggestionItemClick(suggestionModel: SuggestedVendor)
    fun likeUnlike(id: String, like: ImageView, unLike: ImageView)

}
