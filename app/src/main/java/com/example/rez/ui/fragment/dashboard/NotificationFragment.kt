package com.example.rez.ui.fragment.dashboard

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.adapter.paging.BookingPagingAdapter
import com.example.rez.adapter.paging.BookingPagingStateAdapter
import com.example.rez.adapter.paging.NotificationPagingAdapter
import com.example.rez.databinding.FragmentAboutBinding
import com.example.rez.databinding.FragmentNotificationBinding
import com.example.rez.ui.RezViewModel
import com.example.rez.util.visible
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private val rezViewModel: RezViewModel by activityViewModels()
    private lateinit var notificationAdapter: NotificationPagingAdapter
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
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRv()
        loadData()

        binding.btnRetry.setOnClickListener {
            notificationAdapter.retry()
        }

    }

    private fun setRv() {
        notificationAdapter = NotificationPagingAdapter()
        binding.notificationRecyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.notificationRecyclerview.adapter = notificationAdapter
        loaderStateAdapter = BookingPagingStateAdapter { notificationAdapter.retry() }
        binding.notificationRecyclerview.adapter = notificationAdapter.withLoadStateFooter(
            footer = loaderStateAdapter
        )
        binding.notificationRecyclerview.itemAnimator = null
        notificationAdapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                notificationRecyclerview.isVisible = loadState.source.refresh is LoadState.NotLoading
                btnRetry.isVisible = loadState.source.refresh is LoadState.Error
                errorText.isVisible = loadState.source.refresh is LoadState.Error

                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    notificationAdapter.itemCount < 1){
                    notificationRecyclerview.isVisible = false
                    emptyText.isVisible = true
                } else {
                    emptyText.isVisible = false
                }
            }

        }
    }

    private fun loadData() {
        viewLifecycleOwner.lifecycleScope.launch {
            rezViewModel.getNotification("Bearer ${sharedPreferences.getString("token", "token")}").collectLatest {pagingData ->
                binding.progressBar.visible(false)
                notificationAdapter.submitData(pagingData)
            }

        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}