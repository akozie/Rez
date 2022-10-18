package com.example.rez.ui.fragment.dashboard

import android.Manifest
import android.R
import android.app.Activity
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.RezApp
import com.example.rez.adapter.*
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentHomeBinding
import com.example.rez.model.authentication.response.ListClass
import com.example.rez.model.dashboard.*
import com.example.rez.ui.RezViewModel
import com.example.rez.util.handleApiError
import com.example.rez.util.showToast
import com.example.rez.util.visible
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import org.json.JSONObject
import javax.inject.Inject


class Home : Fragment(),OnTopHomeItemClickListener, OnItemClickListener, OnSuggestionClickListener, RecyclerviewAdapter.OnSelectPlace {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var topRecommendedAdapter: TopRecommendedHomeAdapter
    private lateinit var nearRestaurantAdapter: NearRestaurantAdapter
    private lateinit var suggestionRestaurantAdapter: SuggestionRestaurantAdapter
    private lateinit var topList:List<RecommendedVendor>
    private lateinit var nearList:List<NearbyVendor>
    private lateinit var suggestionList:List<SuggestedVendor>
    private val rezViewModel: RezViewModel by activityViewModels()
    private lateinit var locationRequest: LocationRequest
    private lateinit var category: ArrayList<Category>
    private lateinit var restaurantAdapter: CategoryAdapter
    private lateinit var searchRestaurants: AutoCompleteTextView
    private var restaurantID: Int = 0
    private var latitude: Double = 0.0
    private var longitude: Double  = 0.0
    private lateinit var list : ArrayList<ListClass>
    private lateinit var recyclerView: RecyclerView


    lateinit var mView: View
    private var args: RecommendedVendor? = null

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as RezApp).localComponent?.inject(this)
            activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finishAffinity()
                }
            })
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       _binding = FragmentHomeBinding.inflate(inflater, container, false)
       return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // val bundle = Bundle()
        //You need to add the following line for this solution to work; thanks skayred
        //You need to add the following line for this solution to work; thanks skayred
        recyclerView = binding.recyclerview

            locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = 5000
            locationRequest.fastestInterval = 2000
            currentLocation()



        binding?.progressBar?.visible(true)

        searchRestaurants = binding?.selectVenue!!
        searchRestaurants.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(adapterView: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val category  = adapterView?.getItemAtPosition(p2) as Category
                restaurantID = category.id
            }
        }
        searchRestaurants.setDropDownBackgroundDrawable(ColorDrawable(Color.WHITE))


        binding.showAddress.setOnClickListener {
            binding.showAddress.visibility = View.GONE
            binding.edtUserAddress.visibility = View.VISIBLE
            recyclerView.visibility = View.VISIBLE
            binding.fragmentSelectState.visibility = View.GONE
            binding.search.visibility = View.GONE
        }

        binding.search.setOnClickListener {
            val searchText = binding.showAddress.text.toString().trim()
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
//        val check = arguments?.getString("toplist")
//        if(check ==  null){
//            binding?.progressBar?.visible(true)
//        }else{
//            topRestaurants()
//        }
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
        val address = binding.showAddress.text.toString().trim()
        getAddress(address)
        binding.edtUserAddress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(editable: Editable?) {
                getData(editable.toString())
            }
        })
        sendFCMToken()
    }

    fun shouldInterceptBackPress() = true

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//
//        //Save the fragment's state here
//        val gson = Gson()
//        val db = gson.toJson(topList)
//        outState.putString("top", db)
//    }


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
                    getLocations()
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
            if (checkSelfPermission(
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
                                    getLocations()
                                    val address = binding.showAddress.text.toString().trim()
                                    getAddress(address)
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
       // topList =
       // val top = arguments?.getString("toplist") as List<RecommendedVendor>
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
                        is Resource.Error<*> -> {
                            showToast(it.data.toString())
                            rezViewModel.getVendorCategoryResponse.removeObservers(viewLifecycleOwner)                        }
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
                is Resource.Error<*> -> {
                    showToast(it.data.toString())
                    rezViewModel.addOrRemoveFavoritesResponse.removeObservers(viewLifecycleOwner)
                }
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
//                                val bundle = Bundle()
//                                bundle.putParcelable("key", )
                                nearList = it.value.data[0].nearby_vendors
                                val near = gson.toJson(nearList)
                                suggestionList = it.value.data[0].suggested_vendors
                                val suggested = gson.toJson(suggestionList)
                                if (topList.isEmpty()) {
                                    binding?.topRecomendRecycler?.visible(false)
                                    binding?.topLayout?.visible(false)
                                } else if (topList.isNotEmpty()) {
                                    topRestaurants()
                                    sharedPreferences.edit().putString("toplist", db).apply()
                                    sharedPreferences.edit().putString("topguy", it.value.data[0].recommended_vendors[0].category_name).apply()
                                }
                                if (suggestionList.isEmpty()) {
                                    binding?.suggestionRecycler?.visible(false)
                                    binding?.suggestedLayout?.visible(false)
                                    binding?.suggestedView?.visible(false)
                                } else if (suggestionList.isNotEmpty()) {
                                    suggestionRestaurants()
                                    sharedPreferences.edit().putString("suggestedlist", suggested).apply()
                                }
                                if (nearList.isEmpty()) {
                                    binding?.nearRestRecycler?.visible(false)
                                    binding?.nearLayout?.visible(false)
                                    binding?.nearView?.visible(false)
                                } else if (nearList.isNotEmpty()) {
                                    nearRestaurants()
                                    sharedPreferences.edit().putString("nearlist", near).apply()
                                }
                            } else {
                                it.value.message.let { it1 ->
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

    // Locations
//    private fun checkPermissions() {
//        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(requireContext() as Activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1)
//
//        }
//        else {
//            getLocations()
//            val address = binding.showAddress.text.toString().trim()
//            getAddress(address)
//        }
//    }

    private fun getData(text: String) {
        if (text.length >= 4) {
            rezViewModel.getPlace(text, getString(com.example.rez.R.string.api_Key))
            rezViewModel.getPlacesResponse.observe(viewLifecycleOwner, Observer {
                when (it) {
                    is Resource.Success -> {
                        recyclerView.visibility = View.VISIBLE
                        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                        list  = it.value.predictions
                        Log.d("SUCCESS", list.toString())
                        for (i in 0 until it.value.predictions.size){
                            it.value.predictions.get(i)?.let { it1 -> list.add(it1) }
                        }
                        val recyclerviewAdapter = RecyclerviewAdapter(list, this)
                        recyclerviewAdapter.addList(list)
                        recyclerView.adapter = recyclerviewAdapter
                    }
                    is Resource.Failure -> {
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                    }
                }
            })

        }
    }

    private fun getLocations() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        try {
            val address = binding.showAddress.text.toString().trim()
            val geoCoder = Geocoder(requireContext())
            val addressList: List<Address> = geoCoder.getFromLocationName(address, 1)
            if (addressList.isNotEmpty()) {
                val latt = addressList[0].latitude
                val long = addressList[0].longitude
                latitude = latt.toString().toDouble()
                longitude = long.toString().toDouble()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } // end catch
    }

    private fun getAddress(address: String) {
        if (!address.isEmpty()) {
            if (address.length > 2){
                try {
                    val geoCoder = Geocoder(requireContext())
                    val addressList: List<Address> = geoCoder.getFromLocationName(address, 1)!!
                    if (addressList.isNotEmpty()) {
                        val latt = addressList[0].latitude
                        val long = addressList[0].longitude
                        latitude = latt.toString().toDouble()
                        longitude = long.toString().toDouble()

                        Log.d("LAT", latitude.toString())
                        Log.d("LONG", longitude.toString())
//                    Log.d("LONG", "MEEEEEEEEEEE")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else{
                showToast("Text should be more than 2 letters")
            }
            // end catch
        } else{
            //
        }
    }

    private fun sendFCMToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener { token ->
            if (!token.isSuccessful) {
                //return@addOnCompleteListener
            }
            val newToken = token.result //this is the token retrieved
            Log.d("FCM", newToken)
            val fcm = FcmTokenRequest(
                fcm_token = newToken
            )
            if (view != null){
                rezViewModel.fcmToken("Bearer ${sharedPreferences.getString("token", "token")}",fcm)
                rezViewModel.fcmTokenResponse.observe(viewLifecycleOwner) {
                    binding?.progressBar?.visible(it is Resource.Loading)
                    when (it) {
                        is Resource.Success -> {
                            //will delete later, but will toast for development
                            //
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

    override fun onEachPlace(position: String) {
        binding.showAddress.text = position
        binding.showAddress.visibility = View.VISIBLE
        binding.edtUserAddress.visibility = View.GONE
        recyclerView.visibility = View.GONE
        binding.fragmentSelectState.visibility = View.VISIBLE
        binding.search.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
       // requireActivity().finishAffinity()
        //rezViewModel.store()
    }
}