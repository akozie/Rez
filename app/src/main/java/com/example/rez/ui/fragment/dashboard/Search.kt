package com.example.rez.ui.fragment.dashboard

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.RezApp
import com.example.rez.adapter.SearchAdapter
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentSearchBinding
import com.example.rez.model.authentication.response.ResultX
import com.example.rez.ui.RezViewModel
import com.example.rez.util.handleApiError
import com.example.rez.util.showToast
import com.example.rez.util.visible
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.R
import android.content.Context
import android.view.MenuItem
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.rez.model.dashboard.RecommendedVendor


class Search : Fragment(), SearchAdapter.OnSearchClickListener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val rezViewModel: RezViewModel by activityViewModels()
    private lateinit var searchRecyclerview:  RecyclerView
    private lateinit var searchList: List<ResultX>
    private lateinit var searchAdapter: SearchAdapter

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

        binding.searchRestaurants.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(editable: Editable?) {
                search(editable.toString(), "Bearer ${sharedPreferences.getString("token", "token")}")
            }
        })
    }

    private fun search(text: String, token: String){
       if (text.length >= 3){
           rezViewModel.search(text, token)
           rezViewModel.searchResponse.observe(
               viewLifecycleOwner, Observer {
                   binding.progressBar.visible(it is Resource.Loading)
                   when(it) {
                       is Resource.Success -> {
                           if (it.value.status){
                                   searchList = it.value.data.results
                                       searchRecyclerview = binding.searchRecyclerview
                                       searchAdapter = SearchAdapter(searchList, this@Search)
                                       searchRecyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                                       searchRecyclerview.adapter = searchAdapter
                                       binding.emptyResultTable.visible(false)
                                        searchRecyclerview.scrollToPosition(0)
                                        searchRecyclerview.clearFocus()

                           } else {
                               showToast(it.value.message)
                           }
                       }
                       is Resource.Failure -> {
                           // Log.d("CHECK", it)
                           handleApiError(it)
                       }
                   }
               }
           )
       } else{
           showToast("Text too short")
       }
    }

//    override fun onResume() {
//        super.onResume()
//        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
//    }

    override fun onEachSearchClick(resultX: ResultX) {
        val action = SearchDirections.actionSearchToSearchFragment(resultX)
        findNavController().navigate(action)
    }


}