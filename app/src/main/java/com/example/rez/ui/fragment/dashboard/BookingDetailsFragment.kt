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
import androidx.recyclerview.widget.RecyclerView
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentBookingDetailsBinding
import com.example.rez.databinding.FragmentTopRecommendedBinding
import com.example.rez.model.authentication.request.LoginRequest
import com.example.rez.model.authentication.request.TableReviewRequest
import com.example.rez.model.authentication.response.Booking
import com.example.rez.model.dashboard.RecommendedVendor
import com.example.rez.ui.GlideApp
import com.example.rez.ui.RezViewModel
import com.example.rez.util.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import javax.inject.Inject


class BookingDetailsFragment : Fragment() {

    private var _binding : FragmentBookingDetailsBinding? = null
    private val binding get() = _binding!!
    private var args: Booking? = null
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
        _binding = FragmentBookingDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args = arguments?.getParcelable("BOOKINGS")
        setTopData()
        addTableReview()

        binding.vendorRatingBtn.setOnClickListener {
            if (binding.vendorRating.equals(0)) {
                val message = "Rate vendor"
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            } else {
                binding.vendorRatingBtn.enable(true)
                addVendorReviewCheck()
            }
        }

        binding.tableReviewBtn.setOnClickListener {
            if (binding.tableReviewText.text!!.isEmpty() || binding.tableReviewRating.rating.equals(0)) {
                val message = "Table review and table rating is required"
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            } else {
                binding.tableReviewBtn.enable(true)
                addTableReviewCheck()
            }
        }
    }

    private fun addTableReviewCheck() {
        val tableReviewText = binding.tableReviewText.text.toString().trim()
        val tableReviewRating = binding.tableReviewRating.rating.toDouble()
        val formatedRating = Integer.valueOf(tableReviewRating.toInt())

        when {
            tableReviewText.isEmpty() -> {
                showToast("Enter Table Review")
            }
            formatedRating == 0 -> {
                showToast("Rate the table")
            }
            else -> {
                    val newTableReview = TableReviewRequest(
                        rating = formatedRating,
                        review = tableReviewText
                    )
                rezViewModel.addTableReview(token = "Bearer ${sharedPreferences.getString("token", "token")}", newTableReview, args!!.vendor.id, args?.table!!.id)
            }
        }
    }

    private fun addVendorReviewCheck() {
        val vendorReviewRating = binding.tableReviewRating.rating.toDouble()
        val vendorFormattedReviewRating = Integer.valueOf(vendorReviewRating.toInt())

        when {
            vendorFormattedReviewRating == 0 -> {
                showToast("Rate the vendor")
            }
            else -> {
                rezViewModel.addVendorRating(token = "Bearer ${sharedPreferences.getString("token", "token")}", vendorFormattedReviewRating, args!!.vendor.id)
            }
        }
    }


    private fun setTopData() {
        GlideApp.with(requireContext()).load(args?.table?.table_image_url).into(binding.tableImageIv)
        binding.tableNameTv.text = args?.table?.name
        binding.restaurentNameTv.text = args?.vendor?.name
        binding.dateTv.text = args?.booked_for?.substring(0, 10)
        binding.completedTv.text = args?.confirmed_payment?.toString()
        binding.timeTv.text = args?.booked_for?.substring(10)
        if (args?.status == "pending"){
            binding.pendingTv.visible(true)
            binding.acceptedTv.visible(false)
            binding.pendingTv.text = args?.status
        }else if (args?.status == "accepted"){
            binding.acceptedTv.visible(true)
            binding.pendingTv.visible(false)
            binding.acceptedTv.text = args?.status
        }
    }



    private fun addTableReview(){
        rezViewModel.addTableReviewResponse.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)
            when(it) {
                is Resource.Success -> {
                    if (it.value.status){
                        showToast(it.value.message)
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