package com.example.rez.ui.fragment.dashboard

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.api.Resource
import com.example.rez.databinding.ActivityDashboardBinding
import com.example.rez.databinding.FragmentAboutBinding
import com.example.rez.model.dashboard.BookTableRequest
import com.example.rez.model.dashboard.Table
import com.example.rez.ui.GlideApp
import com.example.rez.ui.RezViewModel
import com.example.rez.util.handleApiError
import com.example.rez.util.showToast
import com.example.rez.util.visible
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds


class AboutFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!
    private val rezViewModel: RezViewModel by activityViewModels()
    private  var tableID: Int = 0

    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minutes = 0
    var seconds = 0

    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0
    var savedHour = 0
    var savedMinutes = 0
    var savedSeconds = 0

    private lateinit var date : String
    private lateinit var secondDate : String
    private lateinit var time : String

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
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.proceedTv.setOnClickListener {
//            val action = TableDetailsDirections.actionTableDetailsToProceedToBooking()
//            findNavController().navigate(action)
            pickDate()
        }
       // binding.aboutTextView.text = obj[1].description
      //  binding.aboutTextView.text = sharedPreferences.getString("tableid", "tableid")
        setUpAbout()
    }

    private fun setUpAbout(){
        val vendoID = sharedPreferences.getInt("vendorid", 0)
         tableID = sharedPreferences.getInt("tid", 0)
        rezViewModel.getVendorProfileTable(vendoID, tableID, "Bearer ${sharedPreferences.getString("token", "token")}")
        rezViewModel.getProfileTableResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when(it) {
                is Resource.Success -> {
                    if (it.value.status){
                        binding.aboutTextView.text = it.value.data.description
                    } else {
                        it.value.message?.let { it1 ->
                            Toast.makeText(requireContext(), it1, Toast.LENGTH_SHORT).show() }
                    }
                }
                is Resource.Failure -> handleApiError(it)
            }

        })

    }

    private fun pickDate() {
        getDateTimeCalender()
        DatePickerDialog(requireContext(), this, year, month, day).show()
    }

    private fun getDateTimeCalender() {
        val cal : Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONDAY)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minutes = cal.get(Calendar.MINUTE)
        seconds = cal.get(Calendar.SECOND)
    }



    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        savedDay = p3
        savedMonth = p2 + 1
        savedYear = p1
        if (savedMonth < 10){
            date = "$savedYear-0$savedMonth-$savedDay"
        } else {
            date = "$savedYear-$savedMonth-$savedDay"
        }
        getDateTimeCalender()
        TimePickerDialog(requireContext(), this, hour, minutes, false).show()
    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        savedHour = p1
        savedMinutes = p2
   //     val formatter = SimpleDateFormat("HH:mm:ss")
        if (savedHour <= 12){
            if (savedMinutes < 10){
                time = "0$savedHour:0$savedMinutes"
            }else{
                time = "0$savedHour:$savedMinutes"
            }
        } else {
            time = "$savedHour:$savedMinutes"
        }

        bookTable()

    }

    private fun bookTable() {
        val bTable = BookTableRequest(
            date = date,
            table_id = tableID,
            time = time
        )
        rezViewModel.bookTable("Bearer ${sharedPreferences.getString("token", "token")}", bTable)
        rezViewModel.bookTableResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            // binding.progressBar.visible(it is Resource.Loading)
            when(it) {
                is Resource.Success -> {
                    if (it.value.status){
                        lifecycleScope.launch {
                            val ref = it.value.data.reference
                            val amount = it.value.data.amount
                            sharedPreferences.edit().putString("ref", ref).apply()
                            sharedPreferences.edit().putString("amount", amount).apply()
                            showToast("Proceed to make payment")
                            val action = TableDetailsDirections.actionTableDetailsToProceedToPayment(date, time)
                            findNavController().navigate(action)
                        }
                    } else {
                        val msg = it.value.message
                        showToast(msg)
                    }
                }
                is Resource.Failure -> {
                    val msg = "Date must be a date after $date"
                    showToast(msg)
                    handleApiError(it) { bookTable() }
                }
            }
        })

    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}