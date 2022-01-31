package com.example.rez.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.databinding.TableLayoutBinding
import com.example.rez.model.dashboard.NearRestaurantData
import com.example.rez.model.dashboard.TableData
import com.example.rez.model.dashboard.TableListData
import com.example.rez.ui.GlideApp


class TableAdapter(private var tableList: ArrayList<TableData>, var tableClickListener: OnTableClickListener): RecyclerView.Adapter<TableAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding:TableLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        val capacity = binding.tableCapacityTv
        val pricee = binding.tablePriceTv
        val tableCategoryName = binding.tableNameTv
        val tableCategoryImage = binding.tableImageIv
        val tableDescription = binding.shortDescriptionTv
        val firstCardView = binding.cardView


        val tableImageIv1 = binding.tableImageIv1
        val tableNameTv1 = binding.tableNameTv1
        val shortDescriptionTv1 = binding.shortDescriptionTv1
        val tablePriceTv1 = binding.tablePriceTv1
        val cardView1 = binding.cardView1

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = TableLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.apply {
            with(holder){
                with(tableList[position]){
                    GlideApp.with(context).load(image).into(tableCategoryImage)
                   // tableCategoryImage.setImageResource(image!!)
                    capacity.text = max_people.toString()
                    pricee.text = price
                   // tableCategoryName.text = restaurantName
                    tableDescription.text = description
                }
            }
        }
        holder.itemView.setOnClickListener {
            tableClickListener.onTableItemClick(tableList[position])
        }
    }

    override fun getItemCount(): Int {
        return tableList.size
    }
}

interface OnTableClickListener {
    fun onTableItemClick(tableModel: TableData)
}

