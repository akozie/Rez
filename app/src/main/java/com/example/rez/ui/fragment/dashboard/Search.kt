package com.example.rez.ui.fragment.dashboard

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.rez.RezApp
import com.example.rez.databinding.FragmentSearchBinding
import com.example.rez.model.authentication.response.ResultX
import com.example.rez.ui.RezViewModel
import com.example.rez.util.visible
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.rez.adapter.paging.BookingPagingStateAdapter
import com.example.rez.adapter.paging.SearchPagingAdapter
import com.example.rez.util.showToast
import kotlinx.coroutines.flow.collectLatest
import android.view.KeyEvent
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rez.model.dashboard.SearchModel


class Search : Fragment(), SearchPagingAdapter.OnSearchClickListener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val rezViewModel: RezViewModel by activityViewModels()
    private lateinit var searchAdapter: SearchPagingAdapter
    private lateinit var loaderStateAdapter: BookingPagingStateAdapter
    private var priceTo: String? = null
    private val PRICEFROM: Int = 0
    private var args: SearchModel? = null
    private var stateID: Int = 0
    private var restaurantID: Int = 0
    private lateinit var noOfPersons: String
    private lateinit var searchRestaurants: String
    private var isLoaded = false


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
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            isEnabled = false

            // enable or disable the backpress
        }
        args = arguments?.getParcelable("SEARCHMODEL")
        stateID = 1
        restaurantID = args?.typeID!!
        noOfPersons = "5"
        setRv()

        //searchRestaurants =  binding.searchRestaurantsTextView.text.toString()

        searchRestaurants = args!!.searchText.toString()
        loadData()


        binding.btnRetry.setOnClickListener {
            searchAdapter.retry()
        }


//        binding.search.setOnClickListener {
//            val searchText = binding.restaurantText.text.toString().trim()
//            if (searchText.isNullOrEmpty()){
//                showToast("You must type in a search text")
//            }else if (searchText.length <= 2){
//                showToast("Search text too short")
//            }else{
//                loadData()
//            }
//        }


            binding.searchRestaurantsTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(editable: Editable?) {
                binding.searchRestaurantsTextView.setOnKeyListener(object : View.OnKeyListener {
                    override fun onKey(p0: View?, p1: Int, p2: KeyEvent?): Boolean {
                        if(p2?.action == KeyEvent.ACTION_DOWN && p1 == KeyEvent.KEYCODE_ENTER){
                                if (editable?.length!! <= 2){
                                    showToast("Text length must be greater than 2")
                                } else{
                                    searchRestaurants = editable.toString()
                                    loadData()
                                }
                            return true;
                        }
                        return false;
                    }
                })
            }
        })
    }

    override fun onEachSearchClick(resultX: ResultX) {
        val action = SearchDirections.actionSearchToSearchFragment(resultX)
        findNavController().navigate(action)
    }

    private fun setRv() {
        searchAdapter = SearchPagingAdapter(this)
        binding.searchRecyclerview.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.searchRecyclerview.adapter = searchAdapter
        loaderStateAdapter = BookingPagingStateAdapter { searchAdapter.retry() }
        binding.searchRecyclerview.adapter = searchAdapter.withLoadStateFooter(
            footer = loaderStateAdapter
        )
        binding.searchRecyclerview.itemAnimator = null
        searchAdapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                searchRecyclerview.isVisible = loadState.source.refresh is LoadState.NotLoading
                btnRetry.isVisible = loadState.source.refresh is LoadState.Error
                errorText.isVisible = loadState.source.refresh is LoadState.Error
                noInternetImg.isVisible = loadState.source.refresh is LoadState.Error

                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    searchAdapter.itemCount < 1){
                    searchRecyclerview.isVisible = false
                    emptyText.isVisible = true
                } else {
                    emptyText.isVisible = false
                }
            }
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
                rezViewModel.search(searchRestaurants, null, null, null, null , null,"Bearer ${sharedPreferences.getString("token", "token")}").collectLatest {
                    binding.progressBar.visible(false)
                    searchAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                    //binding.searchLayout.visibility = View.GONE
                    binding.appBarLayout.visibility = View.VISIBLE
           }
        }
    }

//    override fun likeUnlike(id: String, like: ImageView, unLike: ImageView) {
//        registerObservers(like, unLike)
//        rezViewModel.addOrRemoveFavorites(id, "Bearer ${sharedPreferences.getString("token", "token")}")
//    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}