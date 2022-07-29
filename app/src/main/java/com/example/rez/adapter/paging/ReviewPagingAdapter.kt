package com.example.rez.adapter.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.databinding.ReviewShowAdapterLayoutBinding
import com.example.rez.model.dashboard.ReviewX
import com.example.rez.ui.GlideApp

class ReviewPagingAdapter: PagingDataAdapter<ReviewX, ReviewPagingAdapter.BookingMyViewHolder>(
    differCallback
)  {


    inner  class BookingMyViewHolder(private val binding: ReviewShowAdapterLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        val userImage = binding.userImageIv
        val userName = binding.userNameTv
        val reviewMessageTv = binding.reviewMessageTv
        val dateTv = binding.dateTv
    }

    companion object{
        private val differCallback = object : DiffUtil.ItemCallback<ReviewX>(){
            override fun areItemsTheSame(oldItem: ReviewX, newItem: ReviewX): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ReviewX, newItem: ReviewX): Boolean {
                return oldItem == newItem
            }
        }
    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingMyViewHolder {
            val binding = ReviewShowAdapterLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return BookingMyViewHolder(binding)
        }

        override fun onBindViewHolder(holder: BookingMyViewHolder, position: Int) {
            val current = getItem(position)
                holder.itemView.apply {
                    with(current){
                        GlideApp.with(context).load(current?.user?.avatar).into(holder.userImage)
                        holder.userName.text = current?.user?.first_name + " " + current?.user?.last_name
                        holder.reviewMessageTv.text = current?.review
                        // binding.timeTv.text = args?.booked_for?.substring(10)
                        holder.dateTv.text = current?.created_at?.substring(0, 10)
                    }
                }
                }
}





