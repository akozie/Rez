package com.example.rez.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rez.R
import com.example.rez.databinding.NearRestaurantAdapterBinding
import com.example.rez.model.dashboard.NearbyVendor
import com.example.rez.repository.AuthRepository
import com.example.rez.util.visible


class NearRestaurantAdapter(private var nearRestaurantList: List<NearbyVendor>, val clickListener:OnItemClickListener ): RecyclerView.Adapter<NearRestaurantAdapter.MyViewHolder>() {

        private lateinit var binding: NearRestaurantAdapterBinding

    inner class MyViewHolder(binding:NearRestaurantAdapterBinding): RecyclerView.ViewHolder(binding.root) {
        val hotelImage = binding.hotelImageIv
        val unLike = binding.unLikeIv
        val like = binding.likeIv
        val tableQty = binding.tableQtyTv
        val category = binding.categoryTv
        val hotelName = binding.hotelNameTv
        val ratingTv = binding.ratingBar
        val nearDistance = binding.distanceTv
       // val cardView = binding.cardView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
         binding = NearRestaurantAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.itemView.apply {
            with(holder){
                with(nearRestaurantList[position]){
                    if (avatar == null){
                        Glide.with(context).load(R.drawable.table_image).into(hotelImage)
                    } else {
                        Glide.with(context).load(avatar).into(hotelImage)
                    }
                    hotelName.text = company_name
                    if (average_rating.toInt() == 0){
                        ratingTv.rating = "2".toFloat()
                    }else {
                        ratingTv.rating = average_rating
                    }
                    category.text = category_name
                    nearDistance.text = "%.2f".format(distance) +"km"
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
            clickListener.likeUnlike(nearRestaurantList[position].id.toString(), holder.like, holder.unLike)
        }

        holder.like.setOnClickListener {
            clickListener.likeUnlike(nearRestaurantList[position].id.toString(), holder.like, holder.unLike)
        }

        holder.itemView.setOnClickListener {
            clickListener.onItemClick(nearRestaurantList[position])
        }

        holder.hotelName.setOnClickListener {
            clickListener.onIconClick(nearRestaurantList[position])
        }
        holder.nearDistance.setOnClickListener {
            clickListener.onIconClick(nearRestaurantList[position])
        }
    }

    override fun getItemCount(): Int {
        return nearRestaurantList.size
    }

}

interface OnItemClickListener {
    fun onItemClick(nearModel: NearbyVendor)
    fun onIconClick(nearModel: NearbyVendor)
    fun likeUnlike(id: String, like: ImageView, unLike: ImageView)
}

