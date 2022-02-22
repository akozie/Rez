package com.example.rez.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.databinding.BannerListAdapterBinding
import com.example.rez.model.dashboard.BannerData

class BannerAdapter(private var bannerList: ArrayList<BannerData>): RecyclerView.Adapter<BannerAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding: BannerListAdapterBinding): RecyclerView.ViewHolder(binding.root) {

        val bannerImageIv = binding.bannerImageIv
        val bannerNameTv = binding.bannerNameTv
        val descriptionTv = binding.descriptionTv
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = BannerListAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.apply {
            with(holder){
                with(bannerList[position]){
                    bannerImageIv.setImageResource(banner_image)
                    bannerNameTv.text = banner_name
                    descriptionTv.text = description
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return bannerList.size
    }

}

