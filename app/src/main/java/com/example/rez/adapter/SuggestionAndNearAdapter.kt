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


class SuggestionAndNearAdapter(private var suggestionList: List<SuggestedVendor>, val onSuggestionClickListener:OnSuggestionItemClickListener): RecyclerView.Adapter<SuggestionAndNearAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding: SuggestionAndNearLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        val hotelImage = binding.hotelImageIv
        val unLike = binding.unLikeIv
        val like = binding.likeIv
        val tableQty = binding.tableQtyTv
        val category = binding.categoryTv
        val hotelName = binding.hotelNameTv
      //  val addressTv = binding.addressTv
        //val ratingTv = binding.ratingBar
     //   val distanceTv = binding.distanceTv
        // val cardView = binding.cardView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = SuggestionAndNearLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.apply {
            with(holder){
                with(suggestionList[position]){
                    if (avatar != null){
                        Glide.with(context).load(avatar).into(hotelImage)
                    } else if (avatar == null ){
                        Glide.with(context).load(R.drawable.restaurant).into(hotelImage)
                    }
                    hotelName.text = company_name
                   // ratingTv.rating = average_rating
                    category.text = category_name
                    if (total_tables == 1){
                        tableQty.text = total_tables.toString() + " table"
                    }else{
                        tableQty.text = total_tables.toString() + " tables"
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
        holder.itemView.setOnClickListener {
            onSuggestionClickListener.onSuggestionItemClick(suggestionList[position])
        }
        holder.like.setOnClickListener {
            onSuggestionClickListener.likeandUnlike(suggestionList[position].id.toString(), holder.like, holder.unLike)
        }
        holder.unLike.setOnClickListener {
            onSuggestionClickListener.likeandUnlike(suggestionList[position].id.toString(), holder.like, holder.unLike)
        }
    }

    override fun getItemCount(): Int {
        return suggestionList.size
    }
    interface OnSuggestionItemClickListener {
        fun onSuggestionItemClick(suggestedModel: SuggestedVendor)
        fun likeandUnlike(id:String, like:ImageView, unLike: ImageView)
    }
}
