package com.example.rez.ui.fragment.dashboard


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.databinding.FragmentOpeningHoursBinding
import com.example.rez.model.dashboard.DataXXXXXXX
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 * Use the [OpeningHoursFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OpeningHoursFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentOpeningHoursBinding? = null
    private val binding get() = _binding!!
    private var args: DataXXXXXXX? = null

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
        (requireActivity().application as RezApp).localComponent?.inject(this)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOpeningHoursBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args = arguments?.getParcelable("OPENING")

        val address = sharedPreferences.getString("address", "address")

        binding.address.text = address


        if (args?.monday?.isEmpty() == true){
            binding.monText.text = ""
            binding.monSeparator.text = "Closed"
            binding.monText1.visibility = View.GONE
        }else{
            val firstMonText = args?.monday?.get(0)?.substring(0,5)
            val secondMonText = args?.monday?.get(0)?.substring(6)
            if (firstMonText?.substring(0,2)!! < 12.toString()){
                binding.monText.text = firstMonText + " AM"
            } else {
                val newTime = firstMonText.substring(0,2).toInt()
                val calculatedTime = newTime - 12
                val remainingTime = args?.monday?.get(0)?.substring(3,5)
                if (calculatedTime == 0){
                    binding.monText.text = "12:$remainingTime PM"
                }else if (calculatedTime < 10){
                    binding.monText.text = "0$calculatedTime:$remainingTime PM"
                } else{
                    binding.monText.text = "$calculatedTime:$remainingTime PM"
                }
            }
            if (secondMonText?.substring(0,2)!! < 12.toString()){
                binding.monText1.text = secondMonText + " AM"
            } else {
                val newTime = secondMonText.substring(0,2).toInt()
                val calculatedTime = newTime - 12
                val remainingTime = args?.monday?.get(0)?.substring(9)
                if (calculatedTime == 0){
                    binding.monText1.text = "12:$remainingTime PM"
                }else if (calculatedTime < 10){
                    binding.monText1.text = "0$calculatedTime:$remainingTime PM"
                } else{
                    binding.monText1.text = "$calculatedTime:$remainingTime PM"
                }
            }
        }


        if (args?.tuesday?.isEmpty() == true){
            binding.tueText.text = ""
            binding.tueSeparator.text = "Closed"
            binding.tueText1.visibility = View.GONE
        }else{
            val firstTueText = args?.tuesday?.get(0)?.substring(0,5)
            val secondTueText = args?.tuesday?.get(0)?.substring(6)
            if (firstTueText?.substring(0,2)!! < 12.toString()){
                binding.tueText.text = firstTueText + " AM"
            } else {
                val newTime = firstTueText.substring(0,2).toInt()
                val calculatedTime = newTime - 12
                val remainingTime = args?.tuesday?.get(0)?.substring(3,5)
                if (calculatedTime == 0){
                    binding.tueText.text = "12:$remainingTime PM"
                }else if (calculatedTime < 10){
                    binding.tueText.text = "0$calculatedTime:$remainingTime PM"
                } else{
                    binding.tueText.text = "$calculatedTime:$remainingTime PM"
                }
            }
            if (secondTueText?.substring(0,2)!! < 12.toString()){
                binding.tueText1.text = secondTueText + " AM"
            } else {
                val newTime = secondTueText.substring(0,2).toInt()
                val calculatedTime = newTime - 12
                val remainingTime = args?.tuesday?.get(0)?.substring(9)
                if (calculatedTime == 0){
                    binding.tueText1.text = "12:$remainingTime PM"
                }else if (calculatedTime < 10){
                    binding.tueText1.text = "0$calculatedTime:$remainingTime PM"
                } else{
                    binding.tueText1.text = "$calculatedTime:$remainingTime PM"
                }
            }
        }
        if (args?.wednesday?.isEmpty() == true){
            binding.wedText.text = ""
            binding.wedSeparator.text = "Closed"
            binding.wedText1.visibility = View.GONE
        }else{
            val firstWedText = args?.wednesday?.get(0)?.substring(0,5)
            val secondWedText = args?.wednesday?.get(0)?.substring(6)
            if (firstWedText?.substring(0,2)!! < 12.toString()){
                binding.wedText.text = firstWedText + " AM"
            } else {
                val newTime = firstWedText.substring(0,2).toInt()
                val calculatedTime = newTime - 12
                val remainingTime = args?.wednesday?.get(0)?.substring(3,5)
                if (calculatedTime == 0){
                    binding.wedText.text = "12:$remainingTime PM"
                }else if (calculatedTime < 10){
                    binding.wedText.text = "0$calculatedTime:$remainingTime PM"
                } else{
                    binding.wedText.text = "$calculatedTime:$remainingTime PM"
                }
            }
            if (secondWedText?.substring(0,2)!! < 12.toString()){
                binding.wedText1.text = secondWedText + " AM"
            } else {
                val newTime = secondWedText.substring(0,2).toInt()
                val calculatedTime = newTime - 12
                val remainingTime = args?.wednesday?.get(0)?.substring(9)
                if (calculatedTime == 0){
                    binding.wedText1.text = "12:$remainingTime PM"
                }else if (calculatedTime < 10){
                    binding.wedText1.text = "0$calculatedTime:$remainingTime PM"
                } else{
                    binding.wedText1.text = "$calculatedTime:$remainingTime PM"
                }
            }
        }

        if (args?.thursday?.isEmpty() == true){
            binding.thursText.text = ""
            binding.thursSeparator.text = "Closed"
            binding.thursText1.visibility = View.GONE
        }else{
            val firstThursText = args?.thursday?.get(0)?.substring(0,5)
            val secondThursText = args?.thursday?.get(0)?.substring(6)
            if (firstThursText?.substring(0,2)!! < 12.toString()){
                binding.thursText.text = firstThursText + " AM"
            } else {
                val newTime = firstThursText.substring(0,2).toInt()
                val calculatedTime = newTime - 12
                val remainingTime = args?.thursday?.get(0)?.substring(3,5)
                if (calculatedTime == 0){
                    binding.thursText.text = "12:$remainingTime PM"
                }else if (calculatedTime < 10){
                    binding.thursText.text = "0$calculatedTime:$remainingTime PM"
                } else{
                    binding.thursText.text = "$calculatedTime:$remainingTime PM"
                }
            }
            if (secondThursText?.substring(0,2)!! < 12.toString()){
                binding.thursText1.text = secondThursText + " AM"
            } else {
                val newTime = secondThursText.substring(0,2).toInt()
                val calculatedTime = newTime - 12
                val remainingTime = args?.thursday?.get(0)?.substring(9)
                if (calculatedTime == 0){
                    binding.thursText1.text = "12:$remainingTime PM"
                }else if (calculatedTime < 10){
                    binding.thursText1.text = "0$calculatedTime:$remainingTime PM"
                } else{
                    binding.thursText1.text = "$calculatedTime:$remainingTime PM"
                }
            }
        }

        if (args?.friday?.isEmpty() == true){
            binding.friText.text = ""
            binding.friSeparator.text = "Closed"
            binding.friText1.visibility = View.GONE
        }else{
            val firstFriText = args?.friday?.get(0)?.substring(0,5)
            val secondFriText = args?.friday?.get(0)?.substring(6)
            if (firstFriText?.substring(0,2)!! < 12.toString()){
                binding.friText.text = firstFriText + " AM"
            } else {
                val newTime = firstFriText.substring(0,2).toInt()
                val calculatedTime = newTime - 12
                val remainingTime = args?.friday?.get(0)?.substring(3,5)
                if (calculatedTime == 0){
                    binding.friText.text = "12:$remainingTime PM"
                }else if (calculatedTime < 10){
                    binding.friText.text = "0$calculatedTime:$remainingTime PM"
                } else{
                    binding.friText.text = "$calculatedTime:$remainingTime PM"
                }
            }
            if (secondFriText?.substring(0,2)!! < 12.toString()){
                binding.friText1.text = secondFriText + " AM"
            } else {
                val newTime = secondFriText.substring(0,2).toInt()
                val calculatedTime = newTime - 12
                val remainingTime = args?.friday?.get(0)?.substring(9)
                if (calculatedTime == 0){
                    binding.friText1.text = "12:$remainingTime PM"
                }else if (calculatedTime < 10){
                    binding.friText1.text = "0$calculatedTime:$remainingTime PM"
                } else{
                    binding.friText1.text = "$calculatedTime:$remainingTime PM"
                }
            }
        }

        if (args?.saturday?.isEmpty() == true){
            binding.satText.text = ""
            binding.satSeparator.text = "Closed"
            binding.satText1.visibility = View.GONE
        }else{
            val firstSatText = args?.saturday?.get(0)?.substring(0,5)
            val secondSatText = args?.saturday?.get(0)?.substring(6)
            if (firstSatText?.substring(0,2)!! < 12.toString()){
                binding.satText.text = firstSatText + " AM"
            } else {
                val newTime = firstSatText.substring(0,2).toInt()
                val calculatedTime = newTime - 12
                val remainingTime = args?.saturday?.get(0)?.substring(3,5)
                if (calculatedTime == 0){
                    binding.satText.text = "12:$remainingTime PM"
                }else if (calculatedTime < 10){
                    binding.satText.text = "0$calculatedTime:$remainingTime PM"
                } else{
                    binding.satText.text = "$calculatedTime:$remainingTime PM"
                }
            }
            if (secondSatText?.substring(0,2)!! < 12.toString()){
                binding.satText1.text = secondSatText + " AM"
            } else {
                val newTime = secondSatText.substring(0,2).toInt()
                val calculatedTime = newTime - 12
                val remainingTime = args?.saturday?.get(0)?.substring(9)
                if (calculatedTime == 0){
                    binding.satText1.text = "12:$remainingTime PM"
                }else if (calculatedTime < 10){
                    binding.satText1.text = "0$calculatedTime:$remainingTime PM"
                } else{
                    binding.satText1.text = "$calculatedTime:$remainingTime PM"
                }
            }
        }

        if (args?.sunday?.isEmpty() == true){
            binding.sunText.text = ""
            binding.sunSeparator.text = "Closed"
            binding.sunText1.visibility = View.GONE
        }else{
            val firstSunText = args?.sunday?.get(0)?.substring(0,5)
            val secondSunText = args?.sunday?.get(0)?.substring(6)
            if (firstSunText?.substring(0,2)!! < 12.toString()){
                binding.sunText.text = firstSunText + " AM"
            } else {
                val newTime = firstSunText.substring(0,2).toInt()
                val calculatedTime = newTime - 12
                val remainingTime = args?.sunday?.get(0)?.substring(3,5)
                if (calculatedTime == 0){
                    binding.sunText.text = "12:$remainingTime PM"
                }else if (calculatedTime < 10){
                    binding.sunText.text = "0$calculatedTime:$remainingTime PM"
                } else{
                    binding.sunText.text = "$calculatedTime:$remainingTime PM"
                }
            }
            if (secondSunText?.substring(0,2)!! < 12.toString()){
                binding.sunText1.text = secondSunText + " AM"
            } else {
                val newTime = secondSunText.substring(0,2).toInt()
                val calculatedTime = newTime - 12
                val remainingTime = args?.sunday?.get(0)?.substring(9)
                if (calculatedTime == 0){
                    binding.sunText1.text = "12:$remainingTime PM"
                }else if (calculatedTime < 10){
                    binding.sunText1.text = "0$calculatedTime:$remainingTime PM"
                } else{
                    binding.sunText1.text = "$calculatedTime:$remainingTime PM"
                }
            }
        }

    }
}

