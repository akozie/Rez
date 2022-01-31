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
import com.example.rez.adapter.NearRestaurantAdapter
import com.example.rez.adapter.SuggestionAndNearAdapter
import com.example.rez.adapter.TopRecommendedAdapter
import com.example.rez.databinding.FragmentNearRestaurantBinding
import com.example.rez.databinding.FragmentTopRecommendedBinding
import com.example.rez.model.dashboard.NearRestaurantData
import com.example.rez.model.dashboard.SuggestionAndNearData
import com.example.rez.model.dashboard.TopRecommendedData

/**
 * A simple [Fragment] subclass.
 * Use the [NearRestaurant.newInstance] factory method to
 * create an instance of this fragment.
 */
class NearRestaurant : Fragment() {
    private var _binding : FragmentNearRestaurantBinding? = null
    private val binding get() = _binding!!
    private lateinit var nearRestaurantAdapter: SuggestionAndNearAdapter
    private lateinit var nearRecyclerView: RecyclerView
    private lateinit var nearList:ArrayList<SuggestionAndNearData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNearRestaurantBinding.inflate(inflater, container, false)
        nearList()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nearRestaurants()
    }

    private fun nearRestaurants() {
        nearRecyclerView = binding.nearRestRecycler
        nearRestaurantAdapter = SuggestionAndNearAdapter(nearList)
        nearRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        nearRecyclerView.adapter = nearRestaurantAdapter
    }


    private fun nearList() {
        nearList = arrayListOf(
            SuggestionAndNearData("Hustle and bustle", R.drawable.hustle,
                "4.0", "Cafe", "5 Tables", "7771.32km", "80, Amino Kano Crescent, Wuse 2, Abuja", "", ""),
            SuggestionAndNearData("Hustle and bustle", R.drawable.hustle,
                "4.0", "Cafe", "5 Tables", "7771.32km", "80, Amino Kano Crescent, Wuse 2, Abuja", "", ""),
            SuggestionAndNearData("Hustle and bustle", R.drawable.hustle,
                "4.0", "Cafe", "5 Tables", "7771.32km", "80, Amino Kano Crescent, Wuse 2, Abuja", "", "")
        )

    }
}