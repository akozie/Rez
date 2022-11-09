package com.example.rez.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.databinding.PendingReviewsListAdapterBinding
import com.example.rez.databinding.ReviewShowAdapterLayoutBinding
import com.example.rez.model.authentication.response.Booking
import com.example.rez.model.dashboard.ReviewX
import com.example.rez.ui.GlideApp

class PendingReviewsAdapter(private var pendingReviewList: List<Booking>, val onEachPendingReviewsBooking: OnEachPendingReviewsBooking): RecyclerView.Adapter<PendingReviewsAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding: PendingReviewsListAdapterBinding): RecyclerView.ViewHolder(binding.root) {

        val vendorName = binding.vendorName
        val tableName = binding.tableName
        val date = binding.dateTv
        val time = binding.newtimeTv
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = PendingReviewsListAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.apply {
            with(holder){
                with(pendingReviewList[position]){
                    vendorName.text = vendor.name
                    tableName.text = table.name
                    date.text = booked_for.substring(0, 10)
                    time.text = booked_for.substring(10,16)
                }
            }
        }
        holder.itemView.setOnClickListener {
            onEachPendingReviewsBooking.onPendingReviewsItemClick(pendingReviewList[position])
        }
    }

    override fun getItemCount(): Int {
        return pendingReviewList.size
    }
    interface OnEachPendingReviewsBooking {
        fun onPendingReviewsItemClick(booking: Booking)
    }
}


