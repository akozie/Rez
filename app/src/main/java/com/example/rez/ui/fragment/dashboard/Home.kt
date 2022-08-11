package com.example.rez.ui.fragment.dashboard

import android.Manifest
import android.R
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.RezApp
import com.example.rez.adapter.*
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentHomeBinding
import com.example.rez.model.dashboard.*
import com.example.rez.ui.RezViewModel
import com.example.rez.util.handleApiError
import com.example.rez.util.showToast
import com.example.rez.util.visible
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.Serializable
import java.util.*
import javax.inject.Inject


class Home : Fragment(),OnTopHomeItemClickListener, OnItemClickListener, OnSuggestionClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private lateinit var topRecommendedAdapter: TopRecommendedHomeAdapter
    private lateinit var nearRestaurantAdapter: NearRestaurantAdapter
    private lateinit var suggestionRestaurantAdapter: SuggestionRestaurantAdapter
//    private lateinit var topRecyclerView: RecyclerView
//    private lateinit var nearRecyclerView: RecyclerView
//    private lateinit var suggestionRecyclerView: RecyclerView
    private lateinit var topList:List<RecommendedVendor>
    private lateinit var nearList:List<NearbyVendor>
    private lateinit var suggestionList:List<SuggestedVendor>
    private val rezViewModel: RezViewModel by activityViewModels()
    private lateinit var locationRequest: LocationRequest
    private lateinit var category: ArrayList<Category>
    private lateinit var restaurantAdapter: CategoryAdapter
    private lateinit var searchRestaurants: AutoCompleteTextView
    private var restaurantID: Int = 0
    private var latitude: Double  = 0.0
    private var longitude: Double  = 0.0
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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

//    @Deprecated("Deprecated in Java")
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        rezViewModel.getHomeResponse.observe(
//            viewLifecycleOwner, Observer {
//                 binding?.progressBar?.visible(it is Resource.Loading)
//                when(it) {
//                    is Resource.Success -> {
//                        if (it.value.status){
//                            lifecycleScope.launch {
//                                topList = it.value.data[0].recommended_vendors
//                                val gson = Gson()
//                                val db = gson.toJson(topList)
//                                sharedPreferences.edit().putString("toplist", db).apply()
//                                nearList = it.value.data[0].nearby_vendors
//                                val near = gson.toJson(nearList)
//                                sharedPreferences.edit().putString("nearlist", near).apply()
//                                suggestionList = it.value.data[0].suggested_vendors
//                                val suggested = gson.toJson(suggestionList)
//                                sharedPreferences.edit().putString("suggestedlist", suggested).apply()
//                                topRestaurants()
//                                suggestionRestaurants()
//                                if (nearList.isEmpty()){
//                                    binding?.nearRestRecycler?.visible(false)
//                                    binding?.nearLayout?.visible(false)
//                                    binding?.nearView?.visible(false)
//                                }else if(nearList.isNotEmpty()) {
//                                    nearRestaurants()
//                                }
//                            }
//                        } else {
//                            it.value.message?.let { it1 ->
//                                Toast.makeText(requireContext(), it1, Toast.LENGTH_SHORT).show() }
//                        }
//                    }
//                    is Resource.Failure -> {
//                        handleApiError(it) { getVendors()  }
//                    }
//                }
//            }
//        )
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 2000
        currentLocation()
        binding?.progressBar?.visible(true)

        //rezViewModel = ViewModelProvider(activity as ViewModelStoreOwner).get(RezViewModel::class.java)
        searchRestaurants = binding?.selectVenue!!
        searchRestaurants.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(adapterView: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val category  = adapterView?.getItemAtPosition(p2) as Category
                restaurantID = category.id
            }
        }
        searchRestaurants.setDropDownBackgroundDrawable(ColorDrawable(Color.WHITE))


        binding!!.search.setOnClickListener {
            val searchText = binding!!.restaurantText.text.toString().trim()
            if (searchText.isNullOrEmpty()){
                showToast("You must type in a search text")
            }else if (searchText.length <= 2){
                showToast("Search text too short")
            }else{
                val searchModel = SearchModel(
                    searchText = searchText,
                    typeID = restaurantID
                )
                val action = HomeDirections.actionHome2ToSearch(searchModel)
                findNavController().navigate(action)
            }
        }

        binding!!.refreshLayout.setOnRefreshListener {
            getVendors()
            binding!!.refreshLayout.isRefreshing = false
        }


        binding!!.seeAllTopRecommTv.setOnClickListener {
            val action = HomeDirections.actionHome2ToTopRecommended()
            findNavController().navigate(action)
        }

        binding!!.seeAllNearRestTv.setOnClickListener {
            val action = HomeDirections.actionHome2ToNearRestaurant()
            findNavController().navigate(action)
        }

        binding!!.seeAllSuggestionTv.setOnClickListener {
            val action = HomeDirections.actionHome2ToSuggestionForYou()
            findNavController().navigate(action)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (isLoaded){
            rezViewModel.getHomeResponse.observe(
                viewLifecycleOwner, Observer {
                    binding?.progressBar?.visible(it is Resource.Loading)
                    when(it) {
                        is Resource.Success -> {
                            if (it.value.status){
                                lifecycleScope.launch {
                                    topList = it.value.data[0].recommended_vendors
                                    val gson = Gson()
                                    val db = gson.toJson(topList)
                                    sharedPreferences.edit().putString("toplist", db).apply()
                                    nearList = it.value.data[0].nearby_vendors
                                    val near = gson.toJson(nearList)
                                    sharedPreferences.edit().putString("nearlist", near).apply()
                                    suggestionList = it.value.data[0].suggested_vendors
                                    val suggested = gson.toJson(suggestionList)
                                    sharedPreferences.edit().putString("suggestedlist", suggested).apply()
                                    topRestaurants()
                                    suggestionRestaurants()
                                    if (nearList.isEmpty()){
                                        binding?.nearRestRecycler?.visible(false)
                                        binding?.nearLayout?.visible(false)
                                        binding?.nearView?.visible(false)
                                    }else if(nearList.isNotEmpty()) {
                                        nearRestaurants()
                                    }
                                }
                            } else {
                                it.value.message?.let { it1 ->
                                    Toast.makeText(requireContext(), it1, Toast.LENGTH_SHORT).show() }
                            }
                        }
                        is Resource.Failure -> {
                            handleApiError(it) { getVendors()  }
                        }
                    }
                }
            )
        }
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled()) {
                    currentLocation()
                    //getVendors()
                } else {
                    turnOnGPS()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                currentLocation()
                //getVendors()
            }
        }
    }

    private fun currentLocation(){
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (isGPSEnabled()) {
                    LocationServices.getFusedLocationProviderClient(requireContext())
                        .requestLocationUpdates(locationRequest, object : LocationCallback() {
                            override fun onLocationResult(locationResult: LocationResult) {
                                super.onLocationResult(locationResult)
                                activity?.let {
                                    LocationServices.getFusedLocationProviderClient(it)
                                        .removeLocationUpdates(this)
                                }
                                if (locationResult.locations.size > 0) {
                                    val index = locationResult.locations.size - 1
                                     latitude = locationResult.locations[index].latitude
                                     longitude = locationResult.locations[index].longitude
                                    sharedPreferences.edit().putLong("lat", latitude.toBits()).apply()
                                    sharedPreferences.edit().putLong("long", longitude.toBits()).apply()
                                    getVendors()
                                    fetchVendors()
                                }
                            }
                        }, Looper.getMainLooper())
                } else {
                    turnOnGPS()
                }
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }

    private fun turnOnGPS() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        context?.let {
            LocationServices.getSettingsClient(
                it.applicationContext
            )
                .checkLocationSettings(builder.build())
        }?.addOnCompleteListener { task ->
            try {
                val response = task.getResult(
                    ApiException::class.java
                )
                Toast.makeText(requireContext(), "GPS is already turned on", Toast.LENGTH_SHORT)
                    .show()
            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvableApiException = e as ResolvableApiException
                        resolvableApiException.startResolutionForResult(requireActivity(), 2)
                    } catch (ex: IntentSender.SendIntentException) {
                        ex.printStackTrace()
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
                }
            }
        }
    }

    private fun isGPSEnabled(): Boolean {
        var locationManager: LocationManager? = null
        var isEnabled = false
        if (locationManager == null) {
            locationManager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
        }
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return isEnabled
    }

    private fun topRestaurants() {
       // topRecyclerView = binding?.topRecomendRecycler!!
        topRecommendedAdapter = TopRecommendedHomeAdapter(topList, this)
        binding?.topRecomendRecycler?.layoutManager = LinearLayoutManager(parentFragment?.requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding?.topRecomendRecycler?.adapter = topRecommendedAdapter
    }

    private fun nearRestaurants() {
      //  nearRecyclerView = binding?.nearRestRecycler!!
        nearRestaurantAdapter = NearRestaurantAdapter(nearList, this)
        binding?.nearRestRecycler?.layoutManager = LinearLayoutManager(parentFragment?.requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding?.nearRestRecycler?.adapter = nearRestaurantAdapter
    }

    private fun suggestionRestaurants() {
//        suggestionRecyclerView = binding?.suggestionRecycler!!
        suggestionRestaurantAdapter = SuggestionRestaurantAdapter(suggestionList, this)
        binding?.suggestionRecycler?.layoutManager = LinearLayoutManager(parentFragment?.requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding?.suggestionRecycler?.adapter = suggestionRestaurantAdapter
    }




    override fun onItemClick(nearModel: NearbyVendor) {
        val action = HomeDirections.actionHome2ToNearRestFragment(nearModel)
        findNavController().navigate(action)
    }

    override fun onIconClick(nearModel: NearbyVendor) {
        val intent = Intent(requireContext(), GoogleActivity::class.java)
        intent.putExtra("lat", nearModel.lat)
        intent.putExtra("long", nearModel.lng)
        intent.putExtra("name", nearModel.company_name)
        startActivity(intent)
    }

    override fun likeUnlike(id: String, like: ImageView, unLike: ImageView) {
        registerObservers(like, unLike)
        rezViewModel.addOrRemoveFavorites(id, "Bearer ${sharedPreferences.getString("token", "token")}")
    }

    override fun onSuggestionItemClick(suggestionModel: SuggestedVendor) {
        val action = HomeDirections.actionHome2ToSuggestFragment(suggestionModel)
        findNavController().navigate(action)
    }

    private fun fetchVendors(){
        rezViewModel.getVendorCategories()
        if (view != null && activity != null){
            rezViewModel.getVendorCategoryResponse.observe(
                viewLifecycleOwner, Observer {
                    //binding.progressBar.visible(it is Resource.Loading)
                    when(it) {
                        is Resource.Success -> {
                            if (it.value.status) {
                                category = arrayListOf()
                                for (i in 0 until it.value.data.categories.size) {
                                    it.value.data.categories[i].let { it1 -> category.add(it1) }
                                }
                            }
                            restaurantAdapter = CategoryAdapter(
                                category, requireContext(),
                                R.layout.simple_expandable_list_item_1
                            )
                            searchRestaurants.setAdapter(restaurantAdapter)
                        }
                        is Resource.Failure -> handleApiError(it)
                    }
                }
            )
        }
    }



    private fun registerObservers(like: ImageView, unLike: ImageView) {
        rezViewModel.addOrRemoveFavoritesResponse.observe(viewLifecycleOwner) {
            binding?.progressBar?.visible(it is Resource.Loading)
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
                is Resource.Failure -> handleApiError(it)
            }
        }
    }



    private fun removeObserver() {
        rezViewModel.addOrRemoveFavoritesResponse.removeObservers(viewLifecycleOwner)
    }

    private fun getVendors() {
        rezViewModel.getHome(latitude, longitude, token = "Bearer ${sharedPreferences.getString("token", "token")}")
        if (view != null && activity != null){
            parentFragment?.let {
                rezViewModel.getHomeResponse.observe(
                    it.viewLifecycleOwner
                ) {
                    binding?.progressBar?.visible(it is Resource.Loading)
                    when (it) {
                        is Resource.Success -> {
                            if (it.value.status) {
                                topList = it.value.data[0].recommended_vendors
                                val gson = Gson()
                                val db = gson.toJson(topList)
                                sharedPreferences.edit().putString("toplist", db).apply()
                                nearList = it.value.data[0].nearby_vendors
                                val near = gson.toJson(nearList)
                                sharedPreferences.edit().putString("nearlist", near).apply()
                                suggestionList = it.value.data[0].suggested_vendors
                                val suggested = gson.toJson(suggestionList)
                                sharedPreferences.edit().putString("suggestedlist", suggested)
                                    .apply()
                                topRestaurants()
                                suggestionRestaurants()
                                if (nearList.isEmpty()) {
                                    binding?.nearRestRecycler?.visible(false)
                                    binding?.nearLayout?.visible(false)
                                    binding?.nearView?.visible(false)
                                } else if (nearList.isNotEmpty()) {
                                    nearRestaurants()
                                }
                            } else {
                                it.value.message?.let { it1 ->
                                    Toast.makeText(requireContext(), it1, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        is Resource.Failure -> {
                            handleApiError(it) { getVendors() }
                        }
                    }
                }
            }
        }
    }


    override fun onTopItemClick(topModel: RecommendedVendor) {
        val action = HomeDirections.actionHome2ToTableList(topModel)
        findNavController().navigate(action)
    }

//    override fun onStart() {
//        super.onStart()
//        rezViewModel.getHomeResponse.observe(
//            viewLifecycleOwner, Observer {
//             //   binding.progressBar.visible(it is Resource.Loading)
//                when(it) {
//                    is Resource.Success -> {
//                        if (it.value.status){
//                            lifecycleScope.launch {
//                                topList = it.value.data[0].recommended_vendors
//                                val gson = Gson()
//                                val db = gson.toJson(topList)
//                                sharedPreferences.edit().putString("toplist", db).apply()
//                                nearList = it.value.data[0].nearby_vendors
//                                val near = gson.toJson(nearList)
//                                sharedPreferences.edit().putString("nearlist", near).apply()
//                                suggestionList = it.value.data[0].suggested_vendors
//                                val suggested = gson.toJson(suggestionList)
//                                sharedPreferences.edit().putString("suggestedlist", suggested).apply()
//                                topRestaurants()
//                                suggestionRestaurants()
//                                if (nearList.isEmpty()){
//                                    binding?.nearRestRecycler?.visible(false)
//                                    binding?.nearLayout?.visible(false)
//                                    binding?.nearView?.visible(false)
//                                }else if(nearList.isNotEmpty()) {
//                                    nearRestaurants()
//                                }
//                            }
//                        } else {
//                            it.value.message?.let { it1 ->
//                                Toast.makeText(requireContext(), it1, Toast.LENGTH_SHORT).show() }
//                        }
//                    }
//                }
//            }
//        )
//    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putString("top", topList.toString())
//    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}