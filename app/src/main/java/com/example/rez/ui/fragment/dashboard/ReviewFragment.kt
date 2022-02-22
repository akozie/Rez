package com.example.rez.ui.fragment.dashboard

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rez.RezApp
import com.example.rez.adapter.ReviewAdapter
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentReviewBinding
import com.example.rez.model.dashboard.ReviewX
import com.example.rez.ui.RezViewModel
import com.example.rez.util.handleApiError
import com.example.rez.util.visible
import javax.inject.Inject


class ReviewFragment : Fragment() {

    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var reviewList: List<ReviewX>
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
        _binding = FragmentReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpReview()
    }

    private fun setUpReview(){
        val vendoID = sharedPreferences.getInt("vendorid", 0)
        val tableID = sharedPreferences.getInt("tid", 0)
        rezViewModel.getVendorProfileTableReviews(vendoID, tableID, "Bearer ${sharedPreferences.getString("token", "token")}")
        rezViewModel.getProfileTableReviewResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.progressBar.visible(it is Resource.Loading)
            when(it) {
                is Resource.Success -> {
                    if (it.value.status){
                        reviewList = it.value.data.reviews
                        if (reviewList.isEmpty()){
                            binding.emptyReviewTable.visible(true)
                            binding.reviewRecycler.visible(false)
                        }
                        reviewAdapter = ReviewAdapter(reviewList)
                        binding.reviewRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                        binding.reviewRecycler.adapter = reviewAdapter
                    } else {
                        it.value.message?.let { it1 ->
                            Toast.makeText(requireContext(), it1, Toast.LENGTH_SHORT).show() }
                    }
                }
                is Resource.Failure -> handleApiError(it)
            }

        })

    }

}