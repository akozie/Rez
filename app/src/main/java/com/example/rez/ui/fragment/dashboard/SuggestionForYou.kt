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
import com.example.rez.adapter.NearRestaurantAdapter
import com.example.rez.adapter.SuggestionAndNearAdapter
import com.example.rez.adapter.SuggestionRestaurantAdapter
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentNearRestaurantBinding
import com.example.rez.databinding.FragmentSuggestionForYouBinding
import com.example.rez.model.dashboard.*
import com.example.rez.ui.RezViewModel
import com.example.rez.util.handleApiError
import com.example.rez.util.showToast
import com.example.rez.util.visible
import com.google.gson.Gson
import kotlinx.coroutines.launch
import javax.inject.Inject


class SuggestionForYou : Fragment(), SuggestionAndNearAdapter.OnSuggestionItemClickListener {


    private var _binding : FragmentSuggestionForYouBinding? = null
    private val binding get() = _binding!!
    private lateinit var suggestionRestaurantAdapter: SuggestionAndNearAdapter
    private lateinit var suggestionRecyclerView: RecyclerView
    private lateinit var suggestionList:List<SuggestedVendor>
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
        _binding = FragmentSuggestionForYouBinding.inflate(inflater, container, false)
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
                                suggestionList = it.value.data[0].suggested_vendors
                                suggestionRestaurants()
                            }
                        } else {
                            it.value.message?.let { it1 ->
                                Toast.makeText(requireContext(), it1, Toast.LENGTH_SHORT).show() }
                        }
                    }
                    is Resource.Failure -> handleApiError(it)
                }
            }
        )
    }

    private fun suggestionRestaurants() {
        suggestionRecyclerView = binding.suggestionRecycler
        suggestionRestaurantAdapter = SuggestionAndNearAdapter(suggestionList, this)
        suggestionRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        suggestionRecyclerView.adapter = suggestionRestaurantAdapter
      //  binding.progressBar.visible(false)
    }

    override fun onSuggestionItemClick(suggestedModel: SuggestedVendor) {
        val action = SuggestionForYouDirections.actionSuggestionForYouToSuggestFragment(suggestedModel)
        findNavController().navigate(action)
    }

    override fun likeandUnlike(id: String, like: ImageView, unLike: ImageView) {
        registerObservers(like, unLike)
        rezViewModel.addOrRemoveFavorites(id, "Bearer ${sharedPreferences.getString("token", "token")}")
    }

    private fun registerObservers(like: ImageView, unLike: ImageView) {
        rezViewModel.addOrRemoveFavoritesResponse.observe(viewLifecycleOwner, {
            //binding.progressBar.visible(it is Resource.Loading)
            when(it) {
                is Resource.Success -> {
                    if (unLike.isVisible) {
                        showToast("Added Successfully to favorites")
                        //rezViewModel.favoriteResponse = 1
                        like.visibility = View.VISIBLE
                        unLike.visibility = View.INVISIBLE
                        removeObserver()
                    } else if(!unLike.isVisible){
                        showToast("Removed Successfully from favorites")
                        //rezViewModel.favoriteResponse = 0
                        like.visibility = View.INVISIBLE
                        unLike.visibility = View.VISIBLE
                        removeObserver()
                    }
                }
                is Resource.Failure -> handleApiError(it)
            }
        })
    }

    private fun removeObserver() {
        rezViewModel.addOrRemoveFavoritesResponse.removeObservers(viewLifecycleOwner)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}