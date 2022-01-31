package com.example.rez.ui.fragment.dashboard

import android.content.Intent
import android.content.SharedPreferences
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.adapter.FavoritesAdapter
import com.example.rez.adapter.SuggestionAndNearAdapter
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentFavoritesBinding
import com.example.rez.model.authentication.response.Favourite
import com.example.rez.model.authentication.response.GetFavoritesResponse
import com.example.rez.model.dashboard.NearRestaurantData
import com.example.rez.model.dashboard.SuggestionAndNearData
import com.example.rez.ui.RezViewModel
import com.example.rez.ui.activity.DashboardActivity
import com.example.rez.ui.activity.MainActivity
import com.example.rez.util.handleApiError
import com.example.rez.util.showToast
import com.example.rez.util.visible
import com.google.gson.Gson
import kotlinx.coroutines.launch
import javax.inject.Inject


class Favorites : Fragment() {

    private lateinit var _binding : FragmentFavoritesBinding
    private val binding get() = _binding
    private lateinit var nearRestaurantAdapter: FavoritesAdapter
    private lateinit var nearList: List<Favourite>
    private lateinit var nearRecyclerView: RecyclerView
    private lateinit var rezViewModel: RezViewModel

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as RezApp).localComponent?.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        rezViewModel = (activity as DashboardActivity).rezViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rezViewModel.getFavorites("Bearer ${sharedPreferences.getString("token", "token")}")
        rezViewModel.getFavoritesResponse.observe(viewLifecycleOwner, Observer {
          //  binding.progressBar.visible(it is Resource.Loading)
            when(it) {
                is Resource.Success -> {
                    if (it.value.status){
                        lifecycleScope.launch {
                            nearList = it.value.data.favourites
                            nearRecyclerView = binding.favoritesRecycler
                            nearRestaurantAdapter = FavoritesAdapter(nearList)
                            nearRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
                            nearRecyclerView.adapter = nearRestaurantAdapter
                        }
                    } else {
                        it.value.message?.let { it1 -> showToast(it1) }

                    }

                }
                is Resource.Failure -> handleApiError(it)
            }
        })

    }
}