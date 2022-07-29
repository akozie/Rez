package com.example.rez.adapter.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.R
import com.example.rez.databinding.NotificationLayoutBinding
import com.example.rez.databinding.ReservationListAdapterBinding
import com.example.rez.model.authentication.response.Booking
import com.example.rez.model.dashboard.Notification
import com.example.rez.ui.GlideApp
import com.example.rez.util.visible

class NotificationPagingAdapter: PagingDataAdapter<Notification, NotificationPagingAdapter.NotificationMyViewHolder>(
    differCallback
)  {


    inner  class NotificationMyViewHolder(private val binding: NotificationLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        var clientMessage = binding.fragmentMessageClientTextView
        val messageDate = binding.fragmentMessageMessageCreatedAt
        val messageTime = binding.fragmentMessageTime
    }

    companion object{
        private val differCallback = object : DiffUtil.ItemCallback<Notification>(){
            override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
                return oldItem.created_at == newItem.created_at
            }

            override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
                return oldItem == newItem
            }
        }
    }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationMyViewHolder {
            val binding = NotificationLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return NotificationMyViewHolder(binding)
        }

        override fun onBindViewHolder(holder: NotificationMyViewHolder, position: Int) {
            val current = getItem(position)
                holder.itemView.apply {
                    with(holder){
                        clientMessage.text = current?.message
                        messageDate.text = current?.created_at?.substring(0, 10)
                        val newTime = current?.created_at?.substring(11, 16)
                       // messageTime.text = newTime
                        val calTime = newTime?.substring(0,2)?.toInt()
                        val remainingTime = newTime?.substring(3,5)?.toInt()
                        val new = 12 - calTime!!
                        if (newTime != null) {
                            if (newTime < 12.toString()){
                                messageTime.text = newTime + " AM"
                            }else if (new == 0){
                                messageTime.text = "12$remainingTime PM"
                            }else  if (new < 10){
                                messageTime.text = "0$new$remainingTime PM"
                            } else{
                                messageTime.text = "$new$remainingTime PM"
                            }
                        }
                    }
                }
        }
}



