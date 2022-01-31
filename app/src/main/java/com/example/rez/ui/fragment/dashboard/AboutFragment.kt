package com.example.rez.ui.fragment.dashboard

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.navigation.fragment.findNavController
import com.example.rez.R
import com.example.rez.databinding.ActivityDashboardBinding
import com.example.rez.databinding.FragmentAboutBinding
import java.util.*


class AboutFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var _binding: FragmentAboutBinding
    private val binding get() = _binding

    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minutes = 0

    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0
    var savedHour = 0
    var savedMinutes = 0

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
    }



    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        savedDay = p3
        savedMonth = p2 + 1
        savedYear = p1

        getDateTimeCalender()
        TimePickerDialog(requireContext(), this, hour, minutes, false).show()

    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        val action = TableDetailsDirections.actionTableDetailsToProceedToPayment()
        findNavController().navigate(action)
        savedHour = p1
        savedMinutes = p2

      //  setDate.text = "$savedDay-$savedMonth-$savedYear"
//        if (savedHour <= 12){
//            secondDate.text = "$savedHour:$savedMinutes am"
//        } else {
//            val cal = savedHour - 12
//            secondDate.text = "$cal:$savedMinutes pm"
//
//        }
    }
}