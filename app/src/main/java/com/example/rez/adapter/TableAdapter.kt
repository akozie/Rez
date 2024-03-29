package com.example.rez.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.R
import com.example.rez.databinding.TableLayoutBinding
import com.example.rez.model.dashboard.Table
import com.example.rez.ui.GlideApp
import com.example.rez.util.visible


class TableAdapter(private var tableList: List<Table>, var tableClickListener: OnTableClickListener): RecyclerView.Adapter<TableAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding: TableLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        val capacity = binding.tableCapacityTv
        val people = binding.people
        val pricee = binding.tablePriceTv
        val tableCategoryName = binding.tableNameTv
        val tableCategoryImage = binding.tableImageIv
        val tableDescription = binding.shortDescriptionTv
        val firstCardView = binding.cardView

        val capacityTv1 = binding.tableCapacityTv1
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
                    if (status){
                        if (image != null){
                            GlideApp.with(context).load(image).into(tableCategoryImage)
                        }else if (image == null){
                            GlideApp.with(context).load(R.drawable.table_img).into(tableCategoryImage)
                        }
                        if (max_people == 1){
                            capacity.text = max_people.toString()
                            people.text = "Person"
                        }else{
                            capacity.text = max_people.toString()
                        }
                        pricee.text = price
                        tableCategoryName.text = name
                        tableDescription.text = description
                        holder.itemView.setOnClickListener {
                            tableClickListener.onTableItemClick(tableList[position])
                        }
                    } else{
                        cardView1.visible(true)
                        firstCardView.visible(false)
                        GlideApp.with(context).load(R.drawable.table_img).into(tableImageIv1)
                        capacityTv1.text = max_people.toString()
                        tablePriceTv1.text = price
                        tableNameTv1.text = name
                        shortDescriptionTv1.text = description
                    }
                }
            }
        }
    }
    override fun getItemCount(): Int {
        return tableList.size
    }
}
interface OnTableClickListener {
    fun onTableItemClick(tableModel: Table)
}

