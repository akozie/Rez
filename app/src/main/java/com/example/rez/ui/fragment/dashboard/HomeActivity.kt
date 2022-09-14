package com.example.rez.ui.fragment.dashboard

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.adapter.*
import com.example.rez.api.Resource
import com.example.rez.databinding.ActivityDashboardBinding
import com.example.rez.databinding.ActivityHomeBinding
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
import java.util.ArrayList
import javax.inject.Inject
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rez.databinding.FragmentFavoritesCoverBinding
import com.example.rez.repository.AuthRepository
import com.example.rez.ui.RezViewModelProviderFactory

//class HomeActivity : AppCompatActivity(), FavoritesCoverAdapter.OnClickFavoritesCoverItemClickListener {
//
//    private var _binding : ActivityHomeBinding? = null
//    private val binding get() = _binding!!
//    private lateinit var rezViewModel: RezViewModel
//    private lateinit var tableList: List<Favorite>
//    private lateinit var tableAdapter: FavoritesCoverAdapter
//
//    @Inject
//    lateinit var sharedPreferences: SharedPreferences
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        ( application as RezApp).localComponent?.inject(this)
//
//        _binding = ActivityHomeBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        val rezRepository = AuthRepository()
//        val viewModelProviderFactory = RezViewModelProviderFactory(application, rezRepository)
//        rezViewModel = ViewModelProvider(this, viewModelProviderFactory).get(RezViewModel::class.java)
//
//
//        setUpFavorites()
//    }
//
//
//    private fun setUpFavorites(){
//        rezViewModel.getFavorites( "Bearer ${sharedPreferences.getString("token", "token")}")
//        rezViewModel.getFavoritesCover.observe(this, Observer {
//            binding.progressBar.visible(it is Resource.Loading)
//            when(it) {
//                is Resource.Success -> {
//                    if (it.value.status){
//                        tableList = it.value.data.favourites
//                        tableAdapter = FavoritesCoverAdapter(tableList, this)
//                        binding.favoriteCoverRecycler.layoutManager = GridLayoutManager(this, 2)
//                        binding.favoriteCoverRecycler.adapter = tableAdapter
//                        if (tableList.isEmpty()){
//                            binding.favoriteCoverRecycler.visibility = View.GONE
//                            binding.emptyText.visibility = View.VISIBLE
//                        }
//                    } else {
//                        it.value.message.let { it1 ->
//                            Toast.makeText(this, it1, Toast.LENGTH_SHORT).show() }
//                    }
//                }
//               // is Resource.Failure -> handleApiError(it) { setUpFavorites() }
//            }
//        })
//    }
//
//    override fun onEachFavoriteItemClickListener(favorite: Favorite) {
//        val action = FavoritesCoverDirections.actionFavoritesCoverToFavorites(favorite)
//       // findNavController().navigate(action)
//    }
//
//
//}

