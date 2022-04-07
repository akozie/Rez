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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.RezApp
import com.example.rez.databinding.FragmentSearchBinding
import com.example.rez.model.authentication.response.ResultX
import com.example.rez.ui.RezViewModel
import com.example.rez.util.visible
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.os.Handler
import android.view.inputmethod.EditorInfo
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.rez.adapter.BookingPagingStateAdapter
import com.example.rez.adapter.SearchPagingAdapter
import com.example.rez.ui.fragment.ProfileManagementDialogFragments
import com.example.rez.util.showToast
import kotlinx.coroutines.flow.collectLatest
import android.view.KeyEvent

import android.widget.TextView

import android.widget.TextView.OnEditorActionListener
import android.widget.Toast

import android.R

import android.widget.EditText








class Search : Fragment(), SearchPagingAdapter.OnSearchClickListener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val rezViewModel: RezViewModel by activityViewModels()
    private lateinit var searchAdapter: SearchPagingAdapter
    private lateinit var loaderStateAdapter: BookingPagingStateAdapter
    private var noOfPersons: String? = null
    private var priceTo: String? = null
    private val PRICEFROM: Int = 0

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

        setRv()
        //accountFilterDialog()

        binding.btnRetry.setOnClickListener {
            searchAdapter.retry()
        }



//        binding.searchRestaurants.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun afterTextChanged(editable: Editable?) {
//
//                    val handler =  Handler()
//                    handler.postDelayed( Runnable() {
//                        run() {
//                            //Write whatever to want to do after delay specified (3 sec)
//                            if (noOfPersons.isNullOrEmpty() || priceTo.isNullOrEmpty()){
//                                loadData(editable.toString(), 0, PRICEFROM, 0,"Bearer ${sharedPreferences.getString("token", "token")}")
//                            } else{
//                                loadData(editable.toString(), noOfPersons!!.toInt(), PRICEFROM, priceTo!!.toInt(),"Bearer ${sharedPreferences.getString("token", "token")}")
//                            }
//                            Log.d("Handler", "Running Handler");
//                        }
//                    }, 3000)
//            }
//        })

            binding.searchRestaurants.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(editable: Editable?) {
                binding.searchRestaurants.setOnKeyListener(object : View.OnKeyListener {
                    override fun onKey(p0: View?, p1: Int, p2: KeyEvent?): Boolean {
                        if(p2?.action == KeyEvent.ACTION_DOWN && p1 == KeyEvent.KEYCODE_ENTER){
                            //do stuff
                                    //Write whatever to want to do after delay specified (3 sec)
                                    if (noOfPersons.isNullOrEmpty() || priceTo.isNullOrEmpty()){
                                        loadData(editable.toString(), 0, PRICEFROM, 0,"Bearer ${sharedPreferences.getString("token", "token")}")
                                    } else{
                                        loadData(editable.toString(), noOfPersons!!.toInt(), PRICEFROM, priceTo!!.toInt(),"Bearer ${sharedPreferences.getString("token", "token")}")
                                    }
                                    Log.d("Handler", "Running Handler")
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
        binding.searchRecyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
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

    private fun loadData(search: String, noOfPersons: Int, priceFrom: Int, priceTo: Int, token: String) {
        lifecycleScope.launch {
            if (search.isNullOrEmpty()){
                // showToast("Enter name of restaurant")
            } else{
                rezViewModel.search(search, noOfPersons, priceFrom, priceTo, token).collectLatest {
                    binding.progressBar.visible(false)
                    searchAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}