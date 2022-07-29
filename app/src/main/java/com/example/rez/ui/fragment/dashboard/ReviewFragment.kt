package com.example.rez.ui.fragment.dashboard

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.RezApp
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentReviewBinding
import com.example.rez.ui.RezViewModel
import com.example.rez.util.handleApiError
import com.example.rez.util.showToast
import com.example.rez.util.visible
import javax.inject.Inject
import android.content.DialogInterface
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.example.rez.adapter.paging.BookingPagingStateAdapter
import com.example.rez.adapter.paging.ReviewPagingAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ReviewFragment : Fragment() {

    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!
    private lateinit var reviewAdapter: ReviewPagingAdapter
    private val rezViewModel: RezViewModel by activityViewModels()
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
        _binding = FragmentReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRv()
        loadData()

        binding.btnRetry.setOnClickListener {
            reviewAdapter.retry()
        }

        val simpleCallBack = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                AlertDialog.Builder(context)
                    .setTitle("Delete review")
                    .setMessage("Are you sure you want to delete this entry?") // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes,
                        DialogInterface.OnClickListener { _, _ ->
                            // Continue with delete operation
                            rezViewModel.deleteTableReview("Bearer ${sharedPreferences.getString("token", "token")}", sharedPreferences.getInt("reviewid", 0))
                            rezViewModel.deleteTableReviewResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                                binding.progressBar.visible(it is Resource.Loading)
                                when(it) {
                                    is Resource.Success -> {
                                        if (it.value.status){
                                            loadData()
                                            showToast(it.value.message)
                                        } else {
                                            it.value.message?.let { it1 ->
                                                Toast.makeText(requireContext(), it1, Toast.LENGTH_SHORT).show() }
                                        }
                                    }
                                    is Resource.Failure -> handleApiError(it)
                                }

                            })
                        }) // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no,
                        DialogInterface.OnClickListener { _, _ ->
                            loadData()
                        })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            }

        }

        val itemTouchHelper = ItemTouchHelper(simpleCallBack)
        itemTouchHelper.attachToRecyclerView(binding.reviewRecycler)
    }

    private fun setRv() {
        reviewAdapter = ReviewPagingAdapter()
        binding.reviewRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.reviewRecycler.adapter = reviewAdapter
        loaderStateAdapter = BookingPagingStateAdapter { reviewAdapter.retry() }
        binding.reviewRecycler.adapter = reviewAdapter.withLoadStateFooter(
            footer = loaderStateAdapter
        )
        binding.reviewRecycler.itemAnimator = null
        reviewAdapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                reviewRecycler.isVisible = loadState.source.refresh is LoadState.NotLoading
                btnRetry.isVisible = loadState.source.refresh is LoadState.Error
                errorText.isVisible = loadState.source.refresh is LoadState.Error

                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    reviewAdapter.itemCount < 1){
                    reviewRecycler.isVisible = false
                    emptyText.isVisible = true
                } else {
                    emptyText.isVisible = false
                }
            }

        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            val vendorID = sharedPreferences.getInt("vendorid", 0)
        val tableID = sharedPreferences.getInt("tid", 0)
        rezViewModel.getVendorProfileTableReviews(vendorID, tableID, "Bearer ${sharedPreferences.getString("token", "token")}").collectLatest {
                binding.progressBar.visible(false)
                reviewAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}