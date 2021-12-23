package com.example.rez.ui.fragment.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.rez.R
import com.example.rez.databinding.FragmentOTPBinding

class OTPFragment : Fragment() {

    private var _binding: FragmentOTPBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOTPBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragmentEmailConfirmationButton.setOnClickListener {
            val action = OTPFragmentDirections.actionOTPFragmentToLoginFragment()
            findNavController().navigate(action)
        }
    }
}