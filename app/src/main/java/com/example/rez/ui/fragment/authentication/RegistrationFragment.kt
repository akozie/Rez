package com.example.rez.ui.fragment.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.rez.R
import com.example.rez.databinding.FragmentRegistrationBinding

/**
 * A simple [Fragment] subclass.
 * Use the [RegistrationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegistrationFragment : Fragment() {
    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signUpTv.setOnClickListener {
            val action = RegistrationFragmentDirections.actionRegistrationFragmentToOTPFragment()
            findNavController().navigate(action)
        }
        binding.signIn.setOnClickListener {
            val action = RegistrationFragmentDirections.actionRegistrationFragmentToLoginFragment()
            findNavController().navigate(action)
        }
        binding
    }
}