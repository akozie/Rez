package com.example.rez.ui.fragment.dashboard

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.R
import com.example.rez.adapter.NearRestaurantAdapter
import com.example.rez.adapter.SuggestionRestaurantAdapter
import com.example.rez.adapter.TopRecommendedAdapter
import com.example.rez.databinding.FragmentHomeBinding
import com.example.rez.model.dashboard.NearRestaurantData
import com.example.rez.model.dashboard.SuggestionRestaurantData
import com.example.rez.model.dashboard.TopRecommendedData
import com.example.rez.ui.RezViewModel
import com.example.rez.ui.activity.DashboardActivity
import javax.inject.Inject


class Home : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var topRecommendedAdapter: TopRecommendedAdapter
    private lateinit var nearRestaurantAdapter: NearRestaurantAdapter
    private lateinit var suggestionRestaurantAdapter: SuggestionRestaurantAdapter
    private lateinit var topRecyclerView: RecyclerView
    private lateinit var nearRecyclerView: RecyclerView
    private lateinit var suggestionRecyclerView: RecyclerView
    private lateinit var topList:ArrayList<TopRecommendedData>
    private lateinit var nearList:ArrayList<NearRestaurantData>
    private lateinit var suggestionList:ArrayList<SuggestionRestaurantData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        topList()
        nearList()
        suggestionList()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        topRestaurants()
        nearRestaurants()
        suggestionRestaurants()
    }

    private fun topRestaurants() {
        topRecyclerView = binding.topRecomendRecycler
        topRecommendedAdapter = TopRecommendedAdapter(topList)
        topRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        topRecyclerView.adapter = topRecommendedAdapter
    }

    private fun nearRestaurants() {
        nearRecyclerView = binding.nearRestRecycler
        nearRestaurantAdapter = NearRestaurantAdapter(nearList)
        nearRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        nearRecyclerView.adapter = nearRestaurantAdapter
    }

    private fun suggestionRestaurants() {
        suggestionRecyclerView = binding.suggestionRecycler
        suggestionRestaurantAdapter = SuggestionRestaurantAdapter(suggestionList)
        suggestionRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        suggestionRecyclerView.adapter = suggestionRestaurantAdapter
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

    private fun nearList() {
        nearList = arrayListOf(
            NearRestaurantData("Hustle and bustle", R.drawable.hustle,
                "4.0", "Cafe", "5 Tables", "7771.32km", "80, Amino Kano Crescent, Wuse 2, Abuja", "", ""),
            NearRestaurantData("Hustle and bustle", R.drawable.hustle,
                "4.0", "Cafe", "5 Tables", "7771.32km", "80, Amino Kano Crescent, Wuse 2, Abuja", "", ""),
            NearRestaurantData("Hustle and bustle", R.drawable.hustle,
                "4.0", "Cafe", "5 Tables", "7771.32km", "80, Amino Kano Crescent, Wuse 2, Abuja", "", ""),

        )
    }

    private fun suggestionList() {
        suggestionList = arrayListOf(
            SuggestionRestaurantData("Tarragon", R.drawable.restaurant,
                "3.5", "Restaurant", "21 Tables", "777.13km", "1c Ozumba Mbadiwe Ave, Victoria Island 106104, Lagos", "", ""),
           SuggestionRestaurantData("Tarragon", R.drawable.restaurant,
                "3.5", "Restaurant", "21 Tables", "777.13km", "1c Ozumba Mbadiwe Ave, Victoria Island 106104, Lagos", "", ""),
           SuggestionRestaurantData("Tarragon", R.drawable.restaurant,
                "3.5", "Restaurant", "21 Tables", "777.13km", "1c Ozumba Mbadiwe Ave, Victoria Island 106104, Lagos", "", ""),
        )
    }
}