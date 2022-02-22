package com.example.rez.ui.fragment.dashboard

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.RezApp
import com.example.rez.adapter.FavoritesAdapter
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentFavoritesBinding
import com.example.rez.model.authentication.response.Favourite
import com.example.rez.ui.RezViewModel
import com.example.rez.ui.activity.DashboardActivity
import com.example.rez.util.handleApiError
import com.example.rez.util.showToast
import com.example.rez.util.visible
import kotlinx.coroutines.launch
import javax.inject.Inject


class Favorites : Fragment(), FavoritesAdapter.OnClickFavoritesItemClickListener {

    private lateinit var _binding : FragmentFavoritesBinding
    private val binding get() = _binding
    private lateinit var nearRestaurantAdapter: FavoritesAdapter
    private lateinit var favList: List<Favourite>
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

        getFavorites()
    }

    override fun onEachFavoriteItemClickListener(favourite: Favourite) {
        val action = FavoritesDirections.actionFavoritesToFavoriteDetailsFragment(favourite)
        findNavController().navigate(action)
    }

    override fun unLikeFavoritesItem(id: String, like: ImageView) {
        rezViewModel.addOrRemoveFavorites(id, "Bearer ${sharedPreferences.getString("token", "token")}")
        registerObservers(like)

    }

    private fun registerObservers(like: ImageView) {
        rezViewModel.addOrRemoveFavoritesResponse.observe(viewLifecycleOwner, {
            binding.progressBar.visible(it is Resource.Loading)
            when(it) {
                is Resource.Success -> {
                    if (like.isVisible){
                        getFavorites()
                    }

                }
                is Resource.Failure -> handleApiError(it)
            }
        })
    }

    private fun getFavorites(){
        rezViewModel.getFavorites("Bearer ${sharedPreferences.getString("token", "token")}")
        rezViewModel.getFavoritesResponse.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)
            when(it) {
                is Resource.Success -> {
                    if (it.value.status){
                        lifecycleScope.launch {
                             favList = it.value.data.favourites
                            if (favList.isNotEmpty()){
                                nearRecyclerView = binding.favoritesRecycler
                                nearRestaurantAdapter = FavoritesAdapter(favList, this@Favorites)
                                nearRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
                                nearRecyclerView.adapter = nearRestaurantAdapter
                                binding.favoritesEmptyText.visible(false)
                            } else{
                                binding.favoritesRecycler.visible(false)
                                binding.favoritesEmptyText.visible(true)
                            }
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