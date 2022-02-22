package com.example.rez.ui.fragment.dashboard

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.RezApp
import com.example.rez.adapter.BookingPagingAdapter
import com.example.rez.adapter.BookingPagingStateAdapter
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentBookingHistoryBinding
import com.example.rez.model.authentication.response.Booking
import com.example.rez.model.authentication.response.Vendor
import com.example.rez.ui.RezViewModel
import com.example.rez.util.handleApiError
import com.example.rez.util.visible
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject


class BookingHistory : Fragment(), BookingPagingAdapter.OnBookingClickListener {
    private var _binding: FragmentBookingHistoryBinding? = null
    private val binding get() = _binding!!
    private val rezViewModel: RezViewModel by activityViewModels()
    private lateinit var bookingAdapter: BookingPagingAdapter
    private lateinit var loaderStateAdapter: BookingPagingStateAdapter
    private lateinit var bookingListt: ArrayList<Booking>
    private var page = 1
    private val perPage = 20

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
        _binding = FragmentBookingHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //getBookingsHistory()

        setRv()
        loadData()

        binding.btnRetry.setOnClickListener {
            bookingAdapter.retry()
        }
    }



    private fun setRv() {
        bookingAdapter = BookingPagingAdapter(this)
        binding.bookingRecyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.bookingRecyclerview.adapter = bookingAdapter
        loaderStateAdapter = BookingPagingStateAdapter { bookingAdapter.retry() }
        binding.bookingRecyclerview.adapter = bookingAdapter.withLoadStateFooter(
            footer = loaderStateAdapter
        )
        binding.bookingRecyclerview.itemAnimator = null
        bookingAdapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                bookingRecyclerview.isVisible = loadState.source.refresh is LoadState.NotLoading
                btnRetry.isVisible = loadState.source.refresh is LoadState.Error
                errorText.isVisible = loadState.source.refresh is LoadState.Error

                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    bookingAdapter.itemCount < 1){
                    bookingRecyclerview.isVisible = false
                    emptyText.isVisible = true
                } else {
                    emptyText.isVisible = false
                }
            }

        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            // rezViewModel.getBookings("Bearer ${sharedPreferences.getString("token", "token")}")
            rezViewModel.getBookings("Bearer ${sharedPreferences.getString("token", "token")}").collect {
                binding.progressBar.visible(false)
             //   Log.d("BOOKINGSSS", it.toString())
                bookingAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }

        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBookingItemClick(booking: Booking) {
        val action = BookingHistoryDirections.actionBookingHistoryToBookingDetailsFragment(booking)
        findNavController().navigate(action)
    }

}

