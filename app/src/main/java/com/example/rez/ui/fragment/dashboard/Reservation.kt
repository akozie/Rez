package com.example.rez.ui.fragment.dashboard

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rez.RezApp
import com.example.rez.adapter.paging.BookingPagingAdapter
import com.example.rez.adapter.paging.BookingPagingStateAdapter
import com.example.rez.databinding.FragmentReservationBinding
import com.example.rez.model.authentication.response.Booking
import com.example.rez.ui.RezViewModel
import com.example.rez.util.showToast
import com.example.rez.util.visible
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject


class Reservation : Fragment(), BookingPagingAdapter.OnBookingClickListener {

    private var _binding: FragmentReservationBinding? = null
    private val binding get() = _binding!!
    private val rezViewModel: RezViewModel by activityViewModels()
    private lateinit var bookingAdapter: BookingPagingAdapter
    private lateinit var loaderStateAdapter: BookingPagingStateAdapter

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
        _binding = FragmentReservationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.visible(true)

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
                noInternetImg.isVisible = loadState.source.refresh is LoadState.Error


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
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                    rezViewModel.getBookings("Bearer ${sharedPreferences.getString("token", "token")}").collectLatest {pagingData ->
                        binding.progressBar.visible(false)
                        //   Log.d("BOOKINGSSS", it.toString())
                        bookingAdapter.submitData(pagingData)
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBookingItemClick(booking: Booking) {
        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        val formattedDate = df.format(c)
        val currentTime = formattedDate.substring(0,19)
//        println(currentTime) //2022-08-08 12:32:58.139
//        println(currentTime) //2022-08-08 13:00:00
//        Log.d("CHECKKKKKK", currentTime)
        if (currentTime < booking.booked_for){
            val action = ReservationDirections.actionReservationToQRCodeFragment(booking)
            findNavController().navigate(action)
        }else {
            val action = ReservationDirections.actionReservationToBookingDetailsFragment(booking)
            findNavController().navigate(action)
        }

    }

}
