package com.example.rez.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.rez.model.dashboard.Category



class CategoryAdapter(
    categoryList: ArrayList<Category>,
    context: Context,
    layoutId:Int) :
    ArrayAdapter<Category>( context,layoutId,categoryList) {




    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_expandable_list_item_1, parent,false) as TextView


        view.text = super.getItem(position)?.name

        return view

    }

}


