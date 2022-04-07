package com.example.rez.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.rez.R
import com.example.rez.model.dashboard.Data
import com.example.rez.model.dashboard.Image

class TableDetailsViewPagerAdapter(private var context: Context, private var tableDetailsDataList: List<Image>) :
    PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return tableDetailsDataList.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.tab_details_layout, null)

        val imageView: ImageView = view.findViewById(R.id.tab_details_fragment_image)
        val position = tableDetailsDataList[position]
        Glide.with(context).load(position.image_url).into(imageView)
        container.addView(view)
        return view
    }
}
