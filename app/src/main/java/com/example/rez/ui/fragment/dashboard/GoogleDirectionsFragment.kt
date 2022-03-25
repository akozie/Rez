package com.example.rez.ui.fragment.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.view.View
import com.example.rez.databinding.FragmentGoogleDirectionsBinding
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList
import android.app.AlertDialog
import android.app.Application
import android.content.SharedPreferences
import android.graphics.Color
import android.util.Log
import android.widget.RelativeLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.api.Resource
import com.example.rez.databinding.BottomSheetLayoutBinding
import com.example.rez.model.dashboard.NearbyVendor
import com.example.rez.model.direction.DirectionResponseModel
import com.example.rez.model.direction.DirectionRouteModel
import com.example.rez.model.direction.DirectionStepAdapter
import com.example.rez.repository.AuthRepository
import com.example.rez.ui.RezViewModel
import com.example.rez.ui.RezViewModelProviderFactory
import com.example.rez.util.showToast
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import javax.inject.Inject

//class GoogleDirectionsFragment : Fragment(), OnMapReadyCallback {
//
//    private lateinit var _binding: FragmentGoogleDirectionsBinding
//    private val binding get() = _binding
//    private var mGoogleMap: GoogleMap? = null
//  //  private lateinit var appPermissions: AppPermissions
//    private var isLocationPermissionOk = false
//    private var isTrafficEnable = false
//    private lateinit var bottomSheetBehavior: BottomSheetBehavior<RelativeLayout>
//    private lateinit var bottomSheetLayoutBinding: BottomSheetLayoutBinding
//    private lateinit var currentLocation: Location
//    private var endLat: Double? = null
//    private var endLng: Double? = null
//    private var currentLat: Double? = null
//    private var currentLong: Double? = null
//    private var args: NearbyVendor? = null
//    private lateinit var adapterStep: DirectionStepAdapter
//    private lateinit var rezViewModel: RezViewModel
//    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
//    private var permissionToRequest = mutableListOf<String>()
//
//    @Inject
//    lateinit var sharedPreferences: SharedPreferences
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        (requireActivity().application as RezApp).localComponent?.inject(this)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentGoogleDirectionsBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val app = Application()
//        val rezRepository = AuthRepository()
//        val viewModelProviderFactory = RezViewModelProviderFactory(app, rezRepository)
//        rezViewModel = ViewModelProvider(this, viewModelProviderFactory).get(RezViewModel::class.java)
//
//       // args = arguments?.getParcelable("DISTANCE")
//
////        endLat = args?.lat
////        endLng = args?.lng
//
//        endLat = 6.4360139
//        endLng = 3.4585826
//
//        Log.d("LATTTTT", endLat.toString())
//        Log.d("LONGGG", endLng.toString())
//
//        //  supportActionBar?.setDisplayHomeAsUpEnabled(true)
//
//        bottomSheetLayoutBinding = binding.bottomSheet
//        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayoutBinding.root)
//        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//        adapterStep = DirectionStepAdapter()
//
//        bottomSheetLayoutBinding.stepRecyclerView.apply {
//            layoutManager = LinearLayoutManager(requireContext())
//            setHasFixedSize(false)
//            adapter = adapterStep
//        }
//
//        val mapFragment = requireActivity().supportFragmentManager
//            .findFragmentById(R.id.direction_map) as SupportMapFragment?
//
//        mapFragment?.getMapAsync(this)
//
//        binding.enableTraffic.setOnClickListener {
//            if (isTrafficEnable) {
//                mGoogleMap?.isTrafficEnabled = false
//                isTrafficEnable = false
//            } else {
//                mGoogleMap?.isTrafficEnabled = true
//                isTrafficEnable = true
//            }
//        }
//
//
//        binding.travelMode.setOnCheckedChangeListener { _, checked ->
//            if (checked != -1) {
//                when (checked) {
//                    R.id.btnChipDriving -> getDirection("driving")
//                    R.id.btnChipWalking -> getDirection("walking")
//                    R.id.btnChipBike -> getDirection("bicycling")
//                    R.id.btnChipTrain -> getDirection("transit")
//                }
//            }
//        }
//
//        permissionLauncher =
//            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
//                isLocationPermissionOk =
//                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
//                            && permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
//
//                if (isLocationPermissionOk)
//                    setUpGoogleMap()
//                else
//                    Snackbar.make(binding.root, "Location permission denied", Snackbar.LENGTH_LONG)
//                        .show()
//
//            }
//
//    }
////
////    override fun onSupportNavigateUp(): Boolean {
////        onBackPressed()
////        return super.onSupportNavigateUp()
////    }
////
////    override fun onBackPressed() {
////        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
////            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
////        else
////            super.onBackPressed()
////    }
//
//    override fun onMapReady(googleMap: GoogleMap) {
//        mGoogleMap = googleMap
//
//        when {
//            ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED -> {
//                isLocationPermissionOk = true
//                setUpGoogleMap()
//            }
//
//            ActivityCompat.shouldShowRequestPermissionRationale(
//                requireActivity(),
//                android.Manifest.permission.ACCESS_FINE_LOCATION
//            ) -> {
//                AlertDialog.Builder(requireContext())
//                    .setTitle("Location Permission")
//                    .setMessage("Near me required location permission to access your location")
//                    .setPositiveButton("Ok") { _, _ ->
//                        requestLocation()
//                    }.create().show()
//            }
//
//            else -> {
//                requestLocation()
//            }
//        }
//    }
//
//    private fun setUpGoogleMap() {
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            isLocationPermissionOk = false
//            return
//        }
//        mGoogleMap?.isMyLocationEnabled = true
//        mGoogleMap?.uiSettings?.isTiltGesturesEnabled = true
//        mGoogleMap?.uiSettings?.isMyLocationButtonEnabled = false
//        mGoogleMap?.uiSettings?.isCompassEnabled = false
//
//        getCurrentLocation()
//
//    }
//
//    private fun getCurrentLocation() {
//        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
//
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            isLocationPermissionOk = false
//            return
//        }
//        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
//
//            if (it != null) {
//                currentLocation = it
//                getDirection("driving")
//            } else {
//                showToast("Location not found")
//            }
//        }
//    }
//
//    private fun requestLocation() {
//        permissionToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
//        permissionToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
//
//        permissionLauncher.launch(permissionToRequest.toTypedArray())
//    }
//
//    private fun getDirection(mode: String) {
//
//        if (isLocationPermissionOk) {
////            val url = "https://maps.googleapis.com/maps/api/directions/json?" +
////                    "origin=" + currentLocation.latitude + "," + currentLocation.longitude +
////                    "&destination=" + endLat + "," + endLng +
////                    "&mode=" + mode +
//            // "&key=" + resources.getString(R.string.API_KEY)
//          //  currentLat = sharedPreferences.getLong("lat", 0).toDouble()
//          //  currentLong = sharedPreferences.getLong("long", 0).toDouble()
//            Log.d("CURRENTLAT", currentLat.toString())
//            rezViewModel.getDirect(
//                mode,
//                "${currentLocation.latitude}, ${currentLocation.longitude}",
//                "$endLat, $endLng",
//                resources.getString(R.string.api_Key)
//            )
//            rezViewModel.getDirectionResponse.observe(viewLifecycleOwner, Observer {
//                // binding.progressBar.visible(it is Resource.Loading)
//                when (it) {
//                    is Resource.Success -> {
//                        val directionResponseModel: DirectionResponseModel =
//                            it.value
//                        val routeModel: DirectionRouteModel =
//                            directionResponseModel.routes[0]
//
//                        //   supportActionBar!!.title = routeModel.summary
//                        val legModel = routeModel.legs?.get(0)!!
//                        binding.apply {
//                            txtStartLocation.text = legModel.start_address
//                            txtEndLocation.text = legModel.end_address
//                        }
//
//                        bottomSheetLayoutBinding.apply {
//                            txtSheetTime.text = legModel.duration?.text
//                            txtSheetDistance.text = legModel.distance?.text
//                        }
//
//                        adapterStep.setDirectionStepModels(legModel.steps!!)
//
//                        val stepList: MutableList<LatLng> = ArrayList()
//
//                        val options = PolylineOptions().apply {
//                            width(25f)
//                            color(Color.BLUE)
//                            geodesic(true)
//                            clickable(true)
//                            visible(true)
//                        }
//
//                        val pattern: List<PatternItem>
//
//                        if (mode == "walking") {
//                            pattern = listOf(
//                                Dot(), Gap(10f)
//                            )
//
//                            options.jointType(JointType.ROUND)
//                        } else {
//
//                            pattern = listOf(
//                                Dash(30f)
//                            )
//
//                        }
//
//                        options.pattern(pattern)
//                        for (stepModel in legModel.steps) {
//                            val decodedList = decode(stepModel.polyline?.points!!)
//                            for (latLng in decodedList) {
//                                stepList.add(
//                                    LatLng(
//                                        latLng.lat,
//                                        latLng.lng
//                                    )
//                                )
//                            }
//                        }
//
//                        options.addAll(stepList)
//                        mGoogleMap?.addPolyline(options)
//                        val startLocation = LatLng(
//                            legModel.start_location?.lat!!,
//                            legModel.start_location.lng!!
//                        )
//
//                        val endLocation = LatLng(
//                            legModel.end_location?.lat!!,
//                            legModel.end_location.lng!!
//                        )
//
//                        mGoogleMap?.addMarker(
//                            MarkerOptions()
//                                .position(endLocation)
//                                .title("End Location")
//                        )
//
//                        mGoogleMap?.addMarker(
//                            MarkerOptions()
//                                .position(startLocation)
//                                .title("Start Location")
//                        )
//
//                        val builder = LatLngBounds.builder()
//                        builder.include(endLocation).include(startLocation)
//                        val latLngBounds = builder.build()
//
//
//                        mGoogleMap?.animateCamera(
//                            CameraUpdateFactory.newLatLngBounds(
//                                latLngBounds, 0
//                            )
//                        )
//
//                    }
//                    is Resource.Failure -> {
//                        showToast("Error")
////                        binding.g.visible(true)
////                        binding.grid.visible(false)
////                        handleApiError(it)
//                    }
//                }
//            })
//        }
//    }
//
//
//    private fun decode(points: String): List<com.google.maps.model.LatLng> {
//        val len = points.length
//        val path: MutableList<com.google.maps.model.LatLng> = ArrayList(len / 2)
//        var index = 0
//        var lat = 0
//        var lng = 0
//        while (index < len) {
//            var result = 1
//            var shift = 0
//            var b: Int
//            do {
//                b = points[index++].toInt() - 63 - 1
//                result += b shl shift
//                shift += 5
//            } while (b >= 0x1f)
//            lat += if (result and 1 != 0) (result shr 1).inv() else result shr 1
//            result = 1
//            shift = 0
//            do {
//                b = points[index++].toInt() - 63 - 1
//                result += b shl shift
//                shift += 5
//            } while (b >= 0x1f)
//            lng += if (result and 1 != 0) (result shr 1).inv() else result shr 1
//            path.add(com.google.maps.model.LatLng(lat * 1e-5, lng * 1e-5))
//        }
//        return path
//    }
//}