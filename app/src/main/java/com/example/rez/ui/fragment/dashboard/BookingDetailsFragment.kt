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
import com.example.rez.model.authentication.request.RateVendorRequest
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
        binding.tableReviewBtn.button.text = "Submit review"

        args = arguments?.getParcelable("BOOKINGS")
        setTopData()
        addTableReview()
        addVendorReview()

        binding.vendorRatingBtn.submit.setOnClickListener {
                addVendorReviewCheck()
        }

        binding.tableReviewBtn.submit.setOnClickListener {
            if (binding.tableReviewText.text!!.isEmpty() || binding.tableReviewRating.rating.equals(0)) {
                val message = "Table review and table rating is required"
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            } else {
                binding.tableReviewBtn.submit.enable(true)
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
                args!!.vendor.id?.let {
                    rezViewModel.addTableReview(token = "Bearer ${sharedPreferences.getString("token", "token")}", newTableReview,
                        it, args?.table!!.id)
                }
            }
        }
    }

    private fun addVendorReviewCheck() {
        val vendorReviewRating = binding.vendorRating.rating.toDouble()
        val vendorFormattedReviewRating = Integer.valueOf(vendorReviewRating.toInt())

        when {
            vendorFormattedReviewRating == 0 -> {
                showToast("Rate the vendor")
            }
            else -> {
                if (vendorFormattedReviewRating > 0) {
                    val newRating = RateVendorRequest(rating = vendorFormattedReviewRating)
                    args!!.vendor.id?.let {
                        rezViewModel.addVendorRating(
                            token = "Bearer ${
                                sharedPreferences.getString(
                                    "token",
                                    "token"
                                )
                            }", newRating, it
                        )
                    }
                }
            }
        }
    }


    private fun setTopData() {
        if (args?.table?.table_image_url != null){
            GlideApp.with(requireContext()).load(args?.table?.table_image_url).into(binding.tableImageIv)
        } else {
            GlideApp.with(requireContext()).load(R.drawable.restaurant).into(binding.tableImageIv)
        }
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
            binding.tableReviewBtn.progressBar.visible(it is Resource.Loading)
            binding.tableReviewBtn.button.text = "Please wait..."
            when(it) {
                is Resource.Success -> {
                    binding.tableReviewBtn.button.text = "Submit review"
                    if (it.value.status){
                        showToast("Successful, thank you for submitting review")
                    } else {
                        it.value.message.let { it1 ->
                            Toast.makeText(requireContext(), it1, Toast.LENGTH_SHORT).show() }
                    }
                }
                is Resource.Failure -> {
                    binding.tableReviewBtn.button.text = "Submit review"
                    handleApiError(it) { addTableReview() }
                }
            }
        })
    }

    private fun addVendorReview(){
        rezViewModel.addVendorReviewResponse.observe(viewLifecycleOwner, Observer {
            binding.tableReviewBtn.progressBar.visible(it is Resource.Loading)
            binding.tableReviewBtn.button.text = "Please wait..."
            when(it) {
                is Resource.Success -> {
                    binding.tableReviewBtn.button.text = "Submit review"
                    if (it.value.status){
                        showToast(it.value.message)
                    } else {
                        it.value.message.let { it1 ->
                            Toast.makeText(requireContext(), it1, Toast.LENGTH_SHORT).show() }
                    }
                }
                is Resource.Failure -> {
                    binding.tableReviewBtn.button.text = "Submit review"
                    handleApiError(it) { addVendorReview() }
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}