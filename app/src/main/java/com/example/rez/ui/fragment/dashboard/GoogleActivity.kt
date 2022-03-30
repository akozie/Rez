package com.example.rez.ui.fragment.dashboard

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rez.R
import com.example.rez.api.Resource
import com.example.rez.databinding.ActivityGoogleBinding
import com.example.rez.databinding.BottomSheetLayoutBinding
import com.example.rez.model.direction.DirectionLegModel
import com.example.rez.model.direction.DirectionResponseModel
import com.example.rez.model.direction.DirectionRouteModel
import com.example.rez.model.direction.DirectionStepAdapter
import com.example.rez.repository.AuthRepository
import com.example.rez.ui.RezViewModel
import com.example.rez.ui.RezViewModelProviderFactory
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.ArrayList

class GoogleActivity : AppCompatActivity(), OnMapReadyCallback {

  //  private lateinit var appBarConfiguration: AppBarConfiguration
    private var _binding: ActivityGoogleBinding? = null
    private val binding get() = _binding!!
    private var mGoogleMap: GoogleMap? = null
  //  private lateinit var appPermissions: AppPermissions
    private var isLocationPermissionOk = false
    private var isTrafficEnable = false
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<RelativeLayout>
    private lateinit var bottomSheetLayoutBinding: BottomSheetLayoutBinding
    private lateinit var currentLocation: Location
    private var endLat: Double? = null
    private var endLng: Double? = null
    private lateinit var startName: String
    private lateinit var endName: String
    private lateinit var adapterStep: DirectionStepAdapter
    private lateinit var locationViewModel: RezViewModel
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var permissionToRequest = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityGoogleBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setSupportActionBar(binding.toolbar)
//
//        val navController = findNavController(R.id.nav_host_fragment_content_google)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

        val rezRepository = AuthRepository()
        val viewModelProviderFactory = RezViewModelProviderFactory(application, rezRepository)
        locationViewModel = ViewModelProvider(this, viewModelProviderFactory).get(RezViewModel::class.java)

        intent.apply {
            endLat = getDoubleExtra("lat", 0.0)
            endLng = getDoubleExtra("long", 0.0)
            endName = getStringExtra("name").toString()
        }


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
       // appPermissions = AppPermissions()

        bottomSheetLayoutBinding = binding.bottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayoutBinding.root)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        adapterStep = DirectionStepAdapter()

        bottomSheetLayoutBinding.stepRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@GoogleActivity)
            setHasFixedSize(false)
            adapter = adapterStep
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.directionMap) as SupportMapFragment?

        mapFragment?.getMapAsync(this)

        binding.enableTraffic.setOnClickListener {
            if (isTrafficEnable) {
                mGoogleMap?.isTrafficEnabled = false
                isTrafficEnable = false
            } else {
                mGoogleMap?.isTrafficEnabled = true
                isTrafficEnable = true
            }
        }


        binding.travelMode.setOnCheckedChangeListener { _, checked ->
            if (checked != -1) {
                when (checked) {
                    R.id.btnChipDriving -> getDirection("driving")
                }
            }
        }


        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                isLocationPermissionOk =
                    permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
                            && permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true

                if (isLocationPermissionOk)
                    setUpGoogleMap()
                else
                    Snackbar.make(binding.root, "Location permission denied", Snackbar.LENGTH_LONG)
                        .show()

            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        else
            super.onBackPressed()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap

        when {
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                isLocationPermissionOk = true
                setUpGoogleMap()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                AlertDialog.Builder(this)
                    .setTitle("Location Permission")
                    .setMessage("Near me required location permission to access your location")
                    .setPositiveButton("Ok") { _, _ ->
                        requestLocation()
                    }.create().show()
            }

            else -> {
                requestLocation()
            }
        }
    }

    private fun setUpGoogleMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            isLocationPermissionOk = false
            return
        }
        mGoogleMap?.isMyLocationEnabled = true
        mGoogleMap?.uiSettings?.isTiltGesturesEnabled = true
        mGoogleMap?.uiSettings?.isMyLocationButtonEnabled = false
        mGoogleMap?.uiSettings?.isCompassEnabled = false

        getCurrentLocation()

    }

    private fun getCurrentLocation() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            isLocationPermissionOk = false
            return
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {

            if (it != null) {
                currentLocation = it
                getDirection("driving")
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestLocation() {
        permissionToRequest.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
        permissionToRequest.add(android.Manifest.permission.ACCESS_COARSE_LOCATION)

        permissionLauncher.launch(permissionToRequest.toTypedArray())
    }

    private fun getDirection(mode: String) {

        if (isLocationPermissionOk) {
            locationViewModel.getDirect(mode,"${currentLocation.latitude}, ${currentLocation.longitude}", "$endLat, $endLng", resources.getString(R.string.api_Key))
            locationViewModel.getDirectionResponse.observe(this, Observer {
                // binding.progressBar.visible(it is Resource.Loading)
                when(it) {
                    is Resource.Success -> {
                        val directionResponseModel: DirectionResponseModel =
                            it.value
                        val routeModel: DirectionRouteModel =
                            directionResponseModel.routes!![0]

                        val legModel: DirectionLegModel = routeModel.legs?.get(0)!!
                        binding.apply {
                            txtStartLocation.text = legModel.start_address
                            txtEndLocation.text = legModel.end_address
                        }

                        bottomSheetLayoutBinding.apply {
                            txtSheetTime.text = legModel.duration?.text
                            txtSheetDistance.text = legModel.distance?.text
                        }

                        adapterStep.setDirectionStepModels(legModel.steps!!)

                        val stepList: MutableList<LatLng> = ArrayList()

                        val options = PolylineOptions().apply {
                            width(25f)
                            color(Color.BLUE)
                            geodesic(true)
                            clickable(true)
                            visible(true)
                        }

                        val pattern: List<PatternItem>

                        if (mode == "walking") {
                            pattern = listOf(
                                Dot(), Gap(10f)
                            )

                            options.jointType(JointType.ROUND)
                        } else {

                            pattern = listOf(
                                Dash(30f)
                            )

                        }

                        options.pattern(pattern)
                        for (stepModel in legModel.steps) {
                            val decodedList = decode(stepModel.polyline?.points!!)
                            for (latLng in decodedList) {
                                stepList.add(
                                    LatLng(
                                        latLng.lat,
                                        latLng.lng
                                    )
                                )
                            }
                        }

                        options.addAll(stepList)
                        mGoogleMap?.addPolyline(options)
                        val startLocation = LatLng(
                            legModel.start_location?.lat!!,
                            legModel.start_location.lng!!
                        )

                        val endLocation = LatLng(
                            legModel.end_location?.lat!!,
                            legModel.end_location.lng!!
                        )

                        mGoogleMap?.addMarker(
                            MarkerOptions()
                                .position(endLocation)
                                .title(endName)
                        )

                        mGoogleMap?.addMarker(
                            MarkerOptions()
                                .position(startLocation)
                                .title("Me")
                        )

                        val builder = LatLngBounds.builder()
                        builder.include(endLocation).include(startLocation)
                        val latLngBounds = builder.build()


                        mGoogleMap?.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(
                                latLngBounds, 0
                            )
                        )

                    }
                    is Resource.Failure -> {
                    }
                }
            })

        }
    }

    private fun decode(points: String): List<com.google.maps.model.LatLng> {
        val len = points.length
        val path: MutableList<com.google.maps.model.LatLng> = java.util.ArrayList(len / 2)
        var index = 0
        var lat = 0
        var lng = 0
        while (index < len) {
            var result = 1
            var shift = 0
            var b: Int
            do {
                b = points[index++].toInt() - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 0x1f)
            lat += if (result and 1 != 0) (result shr 1).inv() else result shr 1
            result = 1
            shift = 0
            do {
                b = points[index++].toInt() - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 0x1f)
            lng += if (result and 1 != 0) (result shr 1).inv() else result shr 1
            path.add(com.google.maps.model.LatLng(lat * 1e-5, lng * 1e-5))
        }
        return path
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
