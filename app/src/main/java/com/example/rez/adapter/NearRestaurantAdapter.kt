package com.example.rez.adapter


import android.app.Application
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.api.AuthApi
import com.example.rez.databinding.NearRestaurantAdapterBinding
import com.example.rez.model.dashboard.NearRestaurantData
import com.example.rez.model.dashboard.SuggestionAndNearData
import com.example.rez.repository.AuthRepository
import com.example.rez.ui.RezViewModel
import com.example.rez.ui.RezViewModelProviderFactory


class NearRestaurantAdapter(private var nearRestaurantList: ArrayList<SuggestionAndNearData>, var clickListener: OnItemClickListener): RecyclerView.Adapter<NearRestaurantAdapter.MyViewHolder>() {

//    private lateinit var rezViewModel: RezViewModel
//    private lateinit var application: Application
//    val rezRepository = AuthRepository()
//    val viewModelProviderFactory = RezViewModelProviderFactory(application, rezRepository)
//    rezViewModel = ViewModelProvider(this, viewModelProviderFactory).get(RezViewModel::class.java)

//     lateinit var rezRepository: AuthRepository
        private lateinit var binding: NearRestaurantAdapterBinding


    inner class MyViewHolder(binding:NearRestaurantAdapterBinding): RecyclerView.ViewHolder(binding.root) {
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
         binding = NearRestaurantAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

        holder.unLikeIv.setOnClickListener {
//            holder.likeIv.visibility = View.VISIBLE
//            holder.unLikeIv.visibility = View.GONE
//            clickListener.likeUnlike(nearRestaurantList[position].restaurantId!!)
            //rezRepository.addOrRemoveFavorites(nearRestaurantList[position].restaurantId!!)
            clickListener.likeUnlike(nearRestaurantList[position].restaurantId!!, holder.likeIv, holder.unLikeIv)
        }

        holder.likeIv.setOnClickListener {
//            holder.unLikeIv.visibility = View.VISIBLE
//            holder.likeIv.visibility = View.GONE
            clickListener.likeUnlike(nearRestaurantList[position].restaurantId!!, holder.likeIv, holder.unLikeIv)
           // api.addOrRemoveFavorites(nearRestaurantList[position].restaurantId!!)
        }

        holder.itemView.setOnClickListener {
            clickListener.onItemClick(nearRestaurantList[position])
        }
    }

    override fun getItemCount(): Int {
        return nearRestaurantList.size
    }

}

interface OnItemClickListener {
    fun onItemClick(nearModel: SuggestionAndNearData)
    fun likeUnlike(id: String, like: ImageView, unLike: ImageView)
}

