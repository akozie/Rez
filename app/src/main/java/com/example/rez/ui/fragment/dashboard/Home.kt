package com.example.rez.ui.fragment.dashboard

import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.adapter.*
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentHomeBinding
import com.example.rez.databinding.NearRestaurantAdapterBinding
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
import javax.inject.Inject


class Home : Fragment(),OnTopItemClickListener, OnItemClickListener, OnSuggestionClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var topRecommendedAdapter: TopRecommendedAdapter
    private lateinit var nearRestaurantAdapter: NearRestaurantAdapter
    private lateinit var suggestionRestaurantAdapter: SuggestionRestaurantAdapter
    private lateinit var bannerRecyclerView: RecyclerView
    private lateinit var topRecyclerView: RecyclerView
    private lateinit var nearRecyclerView: RecyclerView
    private lateinit var suggestionRecyclerView: RecyclerView
    private lateinit var bannerList:ArrayList<BannerData>
    private lateinit var topList:List<RecommendedVendor>
    private lateinit var nearList:List<NearbyVendor>
    private lateinit var suggestionList:List<SuggestedVendor>
    private val rezViewModel: RezViewModel by activityViewModels()
    private lateinit var locationRequest: LocationRequest
    private var latitude: Double  = 0.0
    private var longitude: Double  = 0.0

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
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 2000
        currentLocation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.visible(true)

        binding.refreshLayout.setOnRefreshListener {
            getVendors()
            binding.refreshLayout.isRefreshing = false
        }

        fetchData()

        binding.seeAllTopRecommTv.setOnClickListener {
            val action = HomeDirections.actionHome2ToTopRecommended()
            findNavController().navigate(action)
        }

        binding.seeAllNearRestTv.setOnClickListener {
            val action = HomeDirections.actionHome2ToNearRestaurant()
            findNavController().navigate(action)
        }

        binding.seeAllSuggestionTv.setOnClickListener {
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
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled()) {
                    currentLocation()
                    getVendors()
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
                getVendors()
            }
        }
    }

    private fun currentLocation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
                                  //  AddressText!!.text = "Latitude: $latitude\nLongitude: $longitude"
                                    getVendors()
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


    private fun banner() {
        bannerRecyclerView = binding.bannerRecycler
        bannerAdapter = BannerAdapter(bannerList)
        bannerRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        bannerRecyclerView.adapter = bannerAdapter
    }

    private fun topRestaurants() {
        topRecyclerView = binding.topRecomendRecycler
        topRecommendedAdapter = TopRecommendedAdapter(topList, this)
        topRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        topRecyclerView.adapter = topRecommendedAdapter
    }

    private fun nearRestaurants() {
        nearRecyclerView = binding.nearRestRecycler
        nearRestaurantAdapter = NearRestaurantAdapter(nearList, this)
        nearRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        nearRecyclerView.adapter = nearRestaurantAdapter
    }

    private fun suggestionRestaurants() {
        suggestionRecyclerView = binding.suggestionRecycler
        suggestionRestaurantAdapter = SuggestionRestaurantAdapter(suggestionList, this)
        suggestionRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        suggestionRecyclerView.adapter = suggestionRestaurantAdapter
    }

    private fun bannerList() {
        bannerList = arrayListOf(
            BannerData("Book Table From Nearby Restaurant", "enjoy table booking by your mobile from anywhere", R.drawable.banner_4),
            BannerData("Book Table From Nearby Restaurant", "enjoy table booking by your mobile from anywhere", R.drawable.banner_2),
            BannerData("Book Table From Nearby Restaurant", "enjoy table booking by your mobile from anywhere", R.drawable.banner_3)
        )
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



    private fun registerObservers(like: ImageView, unLike: ImageView) {
        rezViewModel.addOrRemoveFavoritesResponse.observe(viewLifecycleOwner, {
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

    private fun fetchData() {
        bannerList()
        banner()
    }


    private fun removeObserver() {
        rezViewModel.addOrRemoveFavoritesResponse.removeObservers(viewLifecycleOwner)
    }

    private fun getVendors() {
        rezViewModel.getHome(latitude, longitude, token = "Bearer ${sharedPreferences.getString("token", "token")}")
        rezViewModel.getHomeResponse.observe(
            viewLifecycleOwner, Observer {
                binding.progressBar.visible(it is Resource.Loading)
                when(it) {
                    is Resource.Success -> {
                        if (it.value.status){
                            lifecycleScope.launch {
                                val message = it.value.message
                              //  Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                                topList = it.value.data[0].recommended_vendors
                                val gson = Gson()
                                val db = gson.toJson(topList)
                            //    sharedPreferences.edit().putInt("vendorid", topList[0].id).apply()
                                sharedPreferences.edit().putString("toplist", db).apply()
                                nearList = it.value.data[0].nearby_vendors
                                //nearList = emptyList()
                                val near = gson.toJson(nearList)
                                sharedPreferences.edit().putString("nearlist", near).apply()
                                suggestionList = it.value.data[0].suggested_vendors
                                val suggested = gson.toJson(suggestionList)
                                sharedPreferences.edit().putString("suggestedlist", suggested).apply()
                                topRestaurants()
                                suggestionRestaurants()
                                if (nearList.isEmpty()){
                                    binding.nearRestRecycler.visible(false)
                                    binding.nearLayout.visible(false)
                                    binding.nearView.visible(false)
                                }else if(nearList.isNotEmpty()) {
                                    nearRestaurants()
                                }
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

    override fun onTopItemClick(topModel: RecommendedVendor) {
        val action = HomeDirections.actionHome2ToTableList(topModel)
        findNavController().navigate(action)
    }
}