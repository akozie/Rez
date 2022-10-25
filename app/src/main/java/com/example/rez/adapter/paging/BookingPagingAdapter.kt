package com.example.rez.adapter.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.R
import com.example.rez.databinding.ReservationListAdapterBinding
import com.example.rez.model.authentication.response.Booking
import com.example.rez.ui.GlideApp
import com.example.rez.util.visible

class BookingPagingAdapter(private val onBookingClickListener: OnBookingClickListener): PagingDataAdapter<Booking, BookingPagingAdapter.BookingMyViewHolder>(
    differCallback
)  {


    inner  class BookingMyViewHolder(private val binding: ReservationListAdapterBinding): RecyclerView.ViewHolder(binding.root) {
        val tableImageIv = binding.tableImageIv
        val restaurentNameTv = binding.restaurentNameTv
        val tableNameTv = binding.tableNameTv
        val dateTv = binding.dateTv
        val timeTv = binding.timeTv
        val acceptedTv = binding.acceptedTv
        val pendingTv = binding.pendingTv
        val canceledTv = binding.canceledTv
        val completedTv = binding.completedTv
        val card_view = binding.cardView
    }

    companion object{
        private val differCallback = object : DiffUtil.ItemCallback<Booking>(){
            override fun areItemsTheSame(oldItem: Booking, newItem: Booking): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Booking, newItem: Booking): Boolean {
                return oldItem == newItem
            }
        }
    }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingMyViewHolder {
            val binding = ReservationListAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return BookingMyViewHolder(binding)
        }

        override fun onBindViewHolder(holder: BookingMyViewHolder, position: Int) {
            val current = getItem(position)
                holder.itemView.apply {
                    with(holder){
                        if (current?.table?.table_image_url == null){
                            GlideApp.with(context).load(R.drawable.table_image).into(tableImageIv)
                        } else {
                            GlideApp.with(context).load(current.table.table_image_url).into(tableImageIv)
                        }
                            tableNameTv.text = current?.table?.name
                            restaurentNameTv.text = current?.vendor?.name
                            dateTv.text = current?.booked_for?.substring(0, 10)
                            completedTv.text = current?.confirmed_payment?.toString()
                            timeTv.text = current?.booked_for?.substring(10,15)
                            if (current?.status == "pending"){
                                holder.pendingTv.visible(true)
                                holder.acceptedTv.visible(false)
                                holder.canceledTv.visible(false)
                                pendingTv.text = current.status
                            }else if (current?.status == "accepted"){
                                holder.acceptedTv.visible(true)
                                holder.pendingTv.visible(false)
                                holder.canceledTv.visible(false)
                                acceptedTv.text = current.status
                            } else if (current?.status == "cancelled") {
                                        holder.canceledTv.visible(true)
                                        holder.acceptedTv.visible(false)
                                        holder.pendingTv.visible(false)
                                        canceledTv.text = current.status
                            }
                        }
                    holder.itemView.setOnClickListener {
                        val currentPos = holder.bindingAdapterPosition
                        if (currentPos != RecyclerView.NO_POSITION){
                            onBookingClickListener.onBookingItemClick(current!!)
                        }
                    }
                }
            }
            interface OnBookingClickListener {
            fun onBookingItemClick(booking: Booking)
        }
}



