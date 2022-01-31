package com.example.rez.ui.fragment.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rez.R
import com.example.rez.adapter.ReviewAdapter
import com.example.rez.adapter.TableAdapter
import com.example.rez.databinding.FragmentLoginBinding
import com.example.rez.databinding.FragmentReviewBinding
import com.example.rez.model.dashboard.ReviewData
import com.example.rez.model.dashboard.TableData


class ReviewFragment : Fragment() {

    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var reviewList: ArrayList<ReviewData>

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
        setList()
        setRecyclerview()
    }

    private fun setRecyclerview() {
        reviewAdapter = ReviewAdapter(reviewList)
        binding.reviewRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.reviewRecycler.adapter = reviewAdapter
    }
    private fun setList() {
        reviewList = arrayListOf(
            ReviewData("18th of Jan, 22", "19th of Jan", "John", R.drawable.banner_1, "A good restaurant"),
            ReviewData("18th of Jan, 22", "19th of Jan", "John", R.drawable.banner_1, "A good restaurant"),
            ReviewData("18th of Jan, 22", "19th of Jan", "John", R.drawable.banner_1, "A good restaurant"),
            ReviewData("18th of Jan, 22", "19th of Jan", "John", R.drawable.banner_1, "A good restaurant"),
            ReviewData("18th of Jan, 22", "19th of Jan", "John", R.drawable.banner_1, "A good restaurant"),
            ReviewData("18th of Jan, 22", "19th of Jan", "John", R.drawable.banner_1, "A good restaurant")
        )
    }
}