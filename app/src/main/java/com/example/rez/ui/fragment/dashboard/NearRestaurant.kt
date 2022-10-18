package com.example.rez.ui.fragment.dashboard

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.adapter.NearAdapter
import com.example.rez.adapter.NearRestaurantAdapter
import com.example.rez.adapter.SuggestionAndNearAdapter
import com.example.rez.adapter.TopRecommendedAdapter
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentNearRestaurantBinding
import com.example.rez.databinding.FragmentTopRecommendedBinding
import com.example.rez.model.dashboard.*
import com.example.rez.ui.RezViewModel
import com.example.rez.util.handleApiError
import com.example.rez.util.showToast
import com.example.rez.util.visible
import com.google.gson.Gson
import kotlinx.coroutines.launch
import javax.inject.Inject

class NearRestaurant : Fragment(), NearAdapter.OnNearItemClickListener {
    private var _binding : FragmentNearRestaurantBinding? = null
    private val binding get() = _binding!!
    private lateinit var nearAdapter: NearAdapter
    private lateinit var nearRecyclerView: RecyclerView
    private lateinit var nearList:List<NearbyVendor>
    private val rezViewModel: RezViewModel by activityViewModels()

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
        _binding = FragmentNearRestaurantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getVendors()

    }

    private fun getVendors() {
        rezViewModel.getHome(Double.fromBits(sharedPreferences.getLong("lat", 1)), Double.fromBits(sharedPreferences.getLong("long", 1)), token = "Bearer ${sharedPreferences.getString("token", "token")}")
        rezViewModel.getHomeResponse.observe(
            viewLifecycleOwner, Observer {
              //  binding.progressBar.visible(it is Resource.Loading)
                when(it) {
                    is Resource.Success -> {
                        if (it.value.status){
                            lifecycleScope.launch {
                                nearList = it.value.data[0].nearby_vendors
                                nearRestaurants()
                            }
                        } else {
                            it.value.message.let { it1 ->
                                Toast.makeText(requireContext(), it1, Toast.LENGTH_SHORT).show() }
                        }
                    }
                    is Resource.Error<*> -> {
                        showToast(it.data.toString())
                        rezViewModel.getHomeResponse.removeObservers(viewLifecycleOwner)                    }
                }
            }
        )
    }

    private fun nearRestaurants() {
        nearRecyclerView = binding.nearRestRecycler
        nearAdapter = NearAdapter(nearList, this)
        nearRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        nearRecyclerView.adapter = nearAdapter
    }

    override fun onNearItemClick(nearbyVendor: NearbyVendor) {
        val action = NearRestaurantDirections.actionNearRestaurantToNearRestFragment(nearbyVendor)
        findNavController().navigate(action)
    }

    override fun likeUnlikeNearItem(id: String, like: ImageView, unlike: ImageView) {
        registerObservers(like, unlike)
        rezViewModel.addOrRemoveFavorites(id, "Bearer ${sharedPreferences.getString("token", "token")}")
    }

    private fun registerObservers(like: ImageView, unLike: ImageView) {
        rezViewModel.addOrRemoveFavoritesResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    if (unLike.isVisible) {
                        showToast("Added Successfully to favorites")
                        //rezViewModel.favoriteResponse = 1
                        like.visibility = View.VISIBLE
                        unLike.visibility = View.INVISIBLE
                        removeObserver()
                    } else if (!unLike.isVisible) {
                        showToast("Removed Successfully from favorites")
                        //rezViewModel.favoriteResponse = 0
                        like.visibility = View.INVISIBLE
                        unLike.visibility = View.VISIBLE
                        removeObserver()
                    }
                }
                is Resource.Error<*> -> {
                    showToast(it.data.toString())
                    rezViewModel.addOrRemoveFavoritesResponse.removeObservers(viewLifecycleOwner)                }
            }
        }
    }

    private fun removeObserver() {
        rezViewModel.addOrRemoveFavoritesResponse.removeObservers(viewLifecycleOwner)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}