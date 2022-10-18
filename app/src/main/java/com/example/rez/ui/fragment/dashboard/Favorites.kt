package com.example.rez.ui.fragment.dashboard

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rez.RezApp
import com.example.rez.adapter.paging.BookingPagingStateAdapter
import com.example.rez.adapter.paging.FavoritesPagingAdapter
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentFavoritesBinding
import com.example.rez.model.authentication.response.Favourite
import com.example.rez.model.dashboard.Favorite
import com.example.rez.ui.RezViewModel
import com.example.rez.ui.activity.DashboardActivity
import com.example.rez.util.handleApiError
import com.example.rez.util.showToast
import com.example.rez.util.visible
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


class Favorites : Fragment(), FavoritesPagingAdapter.OnClickFavoritesItemClickListener {

    private var _binding : FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var favoritesAdapter: FavoritesPagingAdapter
    private lateinit var rezViewModel: RezViewModel
    private lateinit var loaderStateAdapter: BookingPagingStateAdapter
    private var args: Favorite? = null


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
        args = arguments?.getParcelable("FAVDATA")
        setRv()
        loadData()

        binding.btnRetry.setOnClickListener {
            favoritesAdapter.retry()
        }
    }

    override fun onEachFavoriteItemClickListener(favorites: Favourite) {
        val action = FavoritesDirections.actionFavoritesToFavoriteDetailsFragment(favorites)
        findNavController().navigate(action)
    }

    override fun unLikeFavoritesItem(id: String, like: ImageView) {
        rezViewModel.addOrRemoveFavorites(id, "Bearer ${sharedPreferences.getString("token", "token")}")
        registerObservers(like)

    }

    private fun registerObservers(like: ImageView) {
        rezViewModel.addOrRemoveFavoritesResponse.observe(viewLifecycleOwner) {
            binding.progressBar.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    if (like.isVisible) {
                        loadData()
                    }

                }
                is Resource.Error<*> -> {
                    showToast(it.data.toString())
                    rezViewModel.addOrRemoveFavoritesResponse.removeObservers(viewLifecycleOwner)
                }
            }
        }
    }


    private fun setRv() {
        favoritesAdapter = FavoritesPagingAdapter(this)
        binding.favoritesRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.favoritesRecycler.adapter = favoritesAdapter
        loaderStateAdapter = BookingPagingStateAdapter { favoritesAdapter.retry() }
        binding.favoritesRecycler.adapter = favoritesAdapter.withLoadStateFooter(
            footer = loaderStateAdapter
        )
        binding.favoritesRecycler.itemAnimator = null
        favoritesAdapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                favoritesRecycler.isVisible = loadState.source.refresh is LoadState.NotLoading
                btnRetry.isVisible = loadState.source.refresh is LoadState.Error
                errorText.isVisible = loadState.source.refresh is LoadState.Error

                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    favoritesAdapter.itemCount < 1){
                    favoritesRecycler.isVisible = false
                    emptyText.isVisible = true
                } else {
                    emptyText.isVisible = false
                }
            }
        }
    }

    private fun loadData() {
        val stateID = args!!.id
        lifecycleScope.launch {
        rezViewModel.getFavoritesState("Bearer ${sharedPreferences.getString("token", "token")}", stateID).collectLatest {
            binding.progressBar.visible(false)
            //   Log.d("BOOKINGSSS", it.toString())
            favoritesAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        }

        }



    override fun onDestroy() {
        super.onDestroy()
        rezViewModel.clean()
        _binding =  null
    }

}