package com.example.rez.ui.fragment.dashboard

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.adapter.PendingReviewsAdapter
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentNotificationBinding
import com.example.rez.databinding.FragmentPendingReviewsBinding
import com.example.rez.model.authentication.response.Booking
import com.example.rez.model.dashboard.Image
import com.example.rez.ui.RezViewModel
import com.example.rez.util.showToast
import com.example.rez.util.visible
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class PendingReviewsFragment : Fragment(), PendingReviewsAdapter.OnEachPendingReviewsBooking {
    private var _binding: FragmentPendingReviewsBinding? = null
    private val binding get() = _binding!!
    private val rezViewModel: RezViewModel by activityViewModels()
    private lateinit var pendingReviewsAdapter: PendingReviewsAdapter
    private lateinit var pendingReviewsList: List<Booking>

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
        _binding = FragmentPendingReviewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setList()
    }

    private fun setList() {
        rezViewModel.pendingReviews("Bearer ${sharedPreferences.getString("token", "token")}")
        rezViewModel.pendingReviewsResponse.observe(
            viewLifecycleOwner, Observer {
                binding.progressBar.visible(it is Resource.Loading)
                when(it) {
                    is Resource.Success -> {
                        if (it.value.status){
                            pendingReviewsList = it.value.data.bookings
                            if (pendingReviewsList.isEmpty()){
                                binding.noPendingReviews.visible(true)
                            }else{
                                setPendingReviewAdapter()
                            }
                        } else {
                            it.value.message.let { it1 ->
                                Toast.makeText(requireContext(), it1, Toast.LENGTH_SHORT).show() }
                        }
                    }
                    is Resource.Error<*> -> {
                        showToast(it.data.toString())
                        rezViewModel.pendingReviewsResponse.removeObservers(viewLifecycleOwner)
                    }
                }
            }
        )
    }

    private fun setPendingReviewAdapter() {
        pendingReviewsAdapter = PendingReviewsAdapter(pendingReviewsList, this)
        binding.pendingRecyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.pendingRecyclerview.adapter = pendingReviewsAdapter
    }


    override fun onPendingReviewsItemClick(booking: Booking) {
        val action = PendingReviewsFragmentDirections.actionPendingReviewsFragmentToBookingDetailsFragment(booking)
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

