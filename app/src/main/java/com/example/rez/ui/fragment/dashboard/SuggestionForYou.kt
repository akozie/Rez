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
import com.example.rez.adapter.SuggestionRestaurantAdapter
import com.example.rez.databinding.FragmentNearRestaurantBinding
import com.example.rez.databinding.FragmentSuggestionForYouBinding
import com.example.rez.model.dashboard.NearRestaurantData
import com.example.rez.model.dashboard.SuggestionAndNearData
import com.example.rez.model.dashboard.SuggestionRestaurantData


class SuggestionForYou : Fragment() {


    private var _binding : FragmentSuggestionForYouBinding? = null
    private val binding get() = _binding!!
    private lateinit var suggestionRestaurantAdapter: SuggestionAndNearAdapter
    private lateinit var suggestionRecyclerView: RecyclerView
    private lateinit var suggestionList:ArrayList<SuggestionAndNearData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSuggestionForYouBinding.inflate(inflater, container, false)
        suggestionList()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        suggestionRestaurants()
    }

    private fun suggestionRestaurants() {
        suggestionRecyclerView = binding.suggestionRecycler
        suggestionRestaurantAdapter = SuggestionAndNearAdapter(suggestionList)
        suggestionRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        suggestionRecyclerView.adapter = suggestionRestaurantAdapter
    }


    private fun suggestionList() {
        suggestionList = arrayListOf(
            SuggestionAndNearData("Tarragon", R.drawable.restaurant,
                "3.5", "Restaurant", "21 Tables", "777.13km", "1c Ozumba Mbadiwe Ave, Victoria Island 106104, Lagos", "", ""),
            SuggestionAndNearData("Tarragon", R.drawable.restaurant,
                "3.5", "Restaurant", "21 Tables", "777.13km", "1c Ozumba Mbadiwe Ave, Victoria Island 106104, Lagos", "", ""),
            SuggestionAndNearData("Tarragon", R.drawable.restaurant,
                "3.5", "Restaurant", "21 Tables", "777.13km", "1c Ozumba Mbadiwe Ave, Victoria Island 106104, Lagos", "", ""),
        )
    }

}