package com.example.rez.ui.fragment.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.R
import com.example.rez.adapter.OnTopItemClickListener
import com.example.rez.adapter.TopRecommendedAdapter
import com.example.rez.databinding.FragmentTopRecommendedBinding
import com.example.rez.model.dashboard.NearRestaurantData
import com.example.rez.model.dashboard.TopRecommendedData


class TopRecommended : Fragment(), OnTopItemClickListener {
    private var _binding : FragmentTopRecommendedBinding? = null
    private val binding get() = _binding!!
    private lateinit var topRecommendedAdapter: TopRecommendedAdapter
    private lateinit var topList:ArrayList<TopRecommendedData>
    private lateinit var topRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTopRecommendedBinding.inflate(inflater, container, false)
         topList()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        topRestaurants()
    }

    private fun topList() {
        topList = arrayListOf(
            TopRecommendedData("1", "Hustle and Bustle", R.drawable.restaurant_img,
                "4", "3", "", "", "", ""),
            TopRecommendedData("1", "Hustle and Bustle", R.drawable.restaurant_img,
                "4", "3", "", "", "", ""),
            TopRecommendedData("1", "Hustle and Bustle", R.drawable.restaurant_img,
                "4", "3", "", "", "", "")
        )
    }

    private fun topRestaurants() {
        topRecyclerView = binding.topRecomendRecycler
        topRecommendedAdapter = TopRecommendedAdapter(topList, this)
        topRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        topRecyclerView.adapter = topRecommendedAdapter
    }

    override fun onTopItemClick(topModel: TopRecommendedData) {
        TODO("Not yet implemented")
    }

}