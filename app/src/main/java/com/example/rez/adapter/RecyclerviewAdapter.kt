package com.example.rez.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.R
import com.example.rez.model.authentication.response.ListClass


class RecyclerviewAdapter(private var list: ArrayList<ListClass>, var onSelectPlace: OnSelectPlace): RecyclerView.Adapter<RecyclerviewAdapter.MyViewHolder>() {

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textLocation = view.findViewById<TextView>(R.id.textView)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_layout, parent, false))

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.apply {
            with(holder){
                with(list[position]){
                    textLocation.text = description
                }
            }
        }
        holder.itemView.setOnClickListener {
            onSelectPlace.onEachPlace(list[position].description)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addList(list: ArrayList<ListClass>) {
        this.list = list
        notifyDataSetChanged()
    }

    interface OnSelectPlace {
        fun onEachPlace(position: String)
    }
}
