package com.example.rez.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rez.R
import com.example.rez.databinding.TableDetailsLayoutBinding
import com.example.rez.databinding.TopRecommendedAdapterBinding
import com.example.rez.model.dashboard.*
import com.squareup.picasso.Picasso

class TableDetailsAdapter( private var tableList:List<DataX>): RecyclerView.Adapter<TableDetailsAdapter.MyViewHolder>() {


    inner class MyViewHolder(private val binding: TableDetailsLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        var hotelImage = binding.hotelImageIv
        var hotelName = binding.tabNameTv
       // var hotelRatingBar = binding.ratingBar
        var tablePrice = binding.tabPriceTv
        var capacity = binding.tabCapacityTv

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = TableDetailsLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.apply {
            with(holder){
                with(tableList[position]){
                  //  hotelName.text = t
                    tablePrice.text = price
                    capacity.text = max_people.toString()
                    if (image == null){
                        Glide.with(context).load(R.drawable.table_image).into(hotelImage)
                    } else {
                        Glide.with(context).load(image).into(hotelImage)
                    }
                   // hotelImage.setImageResource(restaurantImage!!)
                }
            }
        }
//        holder.itemView.setOnClickListener {
//            onTopItemClickListener.onTopItemClick(tableList[position])
//        }
    }

    override fun getItemCount(): Int {
        return tableList.size
    }
}

//interface OnTopItemClickListener {
//    fun onTopItemClick(topModel: RecommendedVendor)
//}

