package com.example.rez.ui.fragment.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.rez.R
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentOTPBinding
import com.example.rez.ui.RezViewModel
import com.example.rez.util.handleApiError
import com.example.rez.util.visible
import kotlinx.coroutines.launch

class OTPFragment : Fragment() {

    private var _binding: FragmentOTPBinding? = null
    private val binding get() = _binding!!
    private val rezViewModel: RezViewModel by activityViewModels()


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
        binding.loginButton.setOnClickListener {
            val action = OTPFragmentDirections.actionOTPFragmentToLoginFragment()
            findNavController().navigate(action)
        }

//        rezViewModel.registerResponse.observe(viewLifecycleOwner, Observer {
//            binding.progressBar.visible(it is Resource.Loading)
//            when(it) {
//                is Resource.Success -> {
//                    lifecycleScope.launch {
//                        val message = it.value.message
//                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
//                    }}
//                is Resource.Failure -> handleApiError(it) { register() }
//            }
//        })
//
//        binding.retryTextView.setOnClickListener {
//
//        }
    }
}