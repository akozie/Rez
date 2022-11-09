package com.example.rez.ui.fragment.dashboard

import android.Manifest
import android.app.Activity
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.R
import com.example.rez.adapter.RecyclerviewAdapter
import com.example.rez.api.Resource
import com.example.rez.model.authentication.response.ListClass
import com.example.rez.model.dashboard.SearchModel
import com.example.rez.util.hideKeyboard


class Search : Fragment(), SearchPagingAdapter.OnSearchClickListener, RecyclerviewAdapter.OnSelectPlace {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val rezViewModel: RezViewModel by activityViewModels()
    private lateinit var searchAdapter: SearchPagingAdapter
    private lateinit var loaderStateAdapter: BookingPagingStateAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var list : ArrayList<ListClass>
    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var newLat: Double? = 0.0
    private var newLng: Double? = 0.0
    private var priceTo: String? = null
    private val PRICEFROM: Int = 0
    private var args: SearchModel? = null
    private var stateID: Int = 0
    private var restaurantID: Int = 0
    private lateinit var noOfPersons: String
    private var searchRestaurants: String? = null
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
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
//            isEnabled = false
//
//        }
        args = arguments?.getParcelable("SEARCHMODEL")
        stateID = 1
        restaurantID = args?.typeID!!
        noOfPersons = "5"
        setRv()

        recyclerView = binding.recyclerview

        binding.showAddress.setOnClickListener {
            binding.showAddress.visibility = View.GONE
            binding.edtUserAddress.visibility = View.VISIBLE
            recyclerView.visibility = View.VISIBLE
            args = null
            hideKeyboard()
        }
        searchRestaurants = args!!.searchText.toString()

        getLocations()
        binding.btnRetry.setOnClickListener {
            searchAdapter.retry()
        }

        binding.edtUserAddress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(editable: Editable?) {
                getData(editable.toString())
            }
        })

    }

    private fun getLocations() {
            val geoCoder = Geocoder(requireContext())
        if (args != null){
            searchRestaurants = args!!.searchText.toString().trim()
            if (searchRestaurants!!.isNullOrEmpty()){
                val address = binding.showAddress.text.toString().trim()
                val addressList: List<Address> = geoCoder.getFromLocationName(address, 1)
                val latt = addressList[0].latitude
                val long = addressList[0].longitude
                lat = latt.toString().toDouble()
                lng = long.toString().toDouble()
                loadData(lat, lng)
            }else{
                val addressList = geoCoder.getFromLocationName(searchRestaurants, 1)
                val latt = addressList[0].latitude
                val long = addressList[0].longitude
                Log.d("LATLNG", latt.toString())
                newLat = latt.toString().toDouble()
                newLng = long.toString().toDouble()
                Log.d("LATLNGGGGG", lat.toString())
                Log.d("LNGGGGG", lng.toString())
                loadData(newLat!!, newLng!!)
                args = null
                newLat = null
                newLng = null
            }
        }else{
            val address = binding.showAddress.text.toString().trim()
            val addressList: List<Address> = geoCoder.getFromLocationName(address, 1)
            val latt = addressList[0].latitude
            val long = addressList[0].longitude
            lat = latt.toString().toDouble()
            lng = long.toString().toDouble()
            loadData(lat, lng)
        }
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

    private fun loadData(lat: Double, lng:Double) {
        lifecycleScope.launch {
            rezViewModel.search(lat,lng,noOfPersons,0,priceTo?.toInt(),stateID,null,"Bearer ${sharedPreferences.getString("token", "token")}").collectLatest {pagingData ->
                binding.progressBar.visible(false)
                searchAdapter.submitData(pagingData)
                args = null
            }
            args = null
        }
        args = null
    }


    private fun getData(text: String) {
        if (text.length >= 4) {
            rezViewModel.getPlace(text, getString(R.string.api_Key))
            rezViewModel.getPlacesResponse.observe(viewLifecycleOwner, Observer {
                when (it) {
                    is Resource.Success -> {
                        recyclerView.visibility = View.VISIBLE
                        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                        list  = it.value.predictions
                        if (list.isEmpty()){
                            recyclerView.visibility = View.GONE
                        }else{
                            Log.d("SUCCESS", list.toString())
                            for (i in 0 until it.value.predictions.size){
                                it.value.predictions.get(i)?.let { it1 -> list.add(it1) }
                            }
                            val recyclerviewAdapter = RecyclerviewAdapter(list, this)
                            recyclerviewAdapter.addList(list)
                            recyclerView.adapter = recyclerviewAdapter
                        }

                    }
                    is Resource.Failure -> {
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                    }
                }
            })

        }
    }

    override fun onEachPlace(position: String) {
        binding.showAddress.text = position
        binding.showAddress.visibility = View.VISIBLE
        binding.edtUserAddress.visibility = View.GONE
        recyclerView.visibility = View.GONE
        args = null
        hideKeyboard()
        getLocations()
    }

    override fun onEachSearchClick(resultX: ResultX) {
        val action = SearchDirections.actionSearchToSearchFragment(resultX)
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}