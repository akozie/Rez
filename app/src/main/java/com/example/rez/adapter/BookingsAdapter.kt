package com.example.rez.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.rez.databinding.ReservationListAdapterBinding
import com.example.rez.model.authentication.response.Booking
import com.example.rez.ui.GlideApp
import com.example.rez.util.visible


//class BookingsAdapter(private var bookingList: ArrayList<Booking>): RecyclerView.Adapter<BookingsAdapter.MyViewHolder>() {
//
//    inner class MyViewHolder(private val binding:ReservationListAdapterBinding): RecyclerView.ViewHolder(binding.root) {
//        val tableImageIv = binding.tableImageIv
//        val restaurentNameTv = binding.restaurentNameTv
//        val tableNameTv = binding.tableNameTv
//        val dateTv = binding.dateTv
//        val timeTv = binding.timeTv
//        val acceptedTv = binding.acceptedTv
//        val pendingTv = binding.pendingTv
//        val canceledTv = binding.canceledTv
//        val completedTv = binding.completedTv
//        val card_view = binding.cardView
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        val binding = ReservationListAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return MyViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        holder.itemView.apply {
//            with(holder){
//                with(bookingList[position]){
//                    GlideApp.with(context).load(table.table_image_url).diskCacheStrategy(
//                        DiskCacheStrategy.ALL).into(tableImageIv)
//                    tableNameTv.text = table.name
//                    restaurentNameTv.text = vendor.name
//                    dateTv.text = booked_for.substring(0, 10)
//                    completedTv.text = confirmed_payment.toString()
//                    timeTv.text = booked_for.substring(10)
//                    if (status == "pending"){
//                        holder.pendingTv.visible(true)
//                        pendingTv.text = status
//                    }else{
//                        holder.acceptedTv.visible(true)
//                        acceptedTv.text = status
//                    }
//                }
////                holder.itemView.setOnClickListener {
////                    onBookingClickListener.onBookingItemClick(bookingList[position])
////                }
//            }
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return bookingList.size
//    }
//
//
//    interface OnBookingClickListener {
//        fun onBookingItemClick(booking: Booking)
//    }
//
//}


