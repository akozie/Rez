package com.example.rez.ui.fragment.dashboard

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.RezApp
import com.example.rez.adapter.OnTopItemClickListener
import com.example.rez.adapter.TopRecommendedAdapter
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentTopRecommendedBinding
import com.example.rez.model.dashboard.RecommendedVendor
import com.example.rez.ui.RezViewModel
import com.example.rez.util.handleApiError
import com.example.rez.util.visible
import com.google.android.gms.location.*
import com.google.gson.Gson
import kotlinx.coroutines.launch
import javax.inject.Inject


class TopRecommended : Fragment(), OnTopItemClickListener {
    private var _binding : FragmentTopRecommendedBinding? = null
    private val binding get() = _binding!!
    private lateinit var topRecommendedAdapter: TopRecommendedAdapter
    private lateinit var topList:List<RecommendedVendor>
    private lateinit var topRecyclerView: RecyclerView
    private val rezViewModel: RezViewModel by activityViewModels()

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
        _binding = FragmentTopRecommendedBinding.inflate(inflater, container, false)
        // topList()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getVendors()
    }

    private fun getVendors() {
        rezViewModel.getHome(Double.fromBits(sharedPreferences.getLong("lat", 1)), Double.fromBits(sharedPreferences.getLong("long", 1)), token = "Bearer ${sharedPreferences.getString("token", "token")}")
        rezViewModel.getHomeResponse.observe(
            viewLifecycleOwner, Observer {
               // binding.progressBar.visible(it is Resource.Loading)
                when(it) {
                    is Resource.Success -> {
                        if (it.value.status){
                            lifecycleScope.launch {
                                topList = it.value.data[0].recommended_vendors
                                topRestaurants()
                            }
                        } else {
                            it.value.message?.let { it1 ->
                                Toast.makeText(requireContext(), it1, Toast.LENGTH_SHORT).show() }
                        }
                    }
                    is Resource.Failure -> handleApiError(it) { getVendors() }
                }
            }
        )
    }

    private fun topRestaurants() {
        topRecyclerView = binding.topRecomendRecycler
        topRecommendedAdapter = TopRecommendedAdapter(topList, this)
        topRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        topRecyclerView.adapter = topRecommendedAdapter
        //binding.progressBar.visibility = View.GONE
    }

    override fun onTopItemClick(topModel: RecommendedVendor) {
        val action = TopRecommendedDirections.actionTopRecommendedToTopFragment(topModel)
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}