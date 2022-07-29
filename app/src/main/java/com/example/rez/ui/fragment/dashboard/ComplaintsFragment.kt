package com.example.rez.ui.fragment.dashboard

import android.content.Intent
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
import androidx.navigation.fragment.findNavController
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentComplaintsBinding
import com.example.rez.databinding.FragmentTableListBinding
import com.example.rez.model.authentication.request.ChangePasswordRequest
import com.example.rez.model.dashboard.ComplaintRequest
import com.example.rez.ui.RezViewModel
import com.example.rez.ui.activity.MainActivity
import com.example.rez.util.enable
import com.example.rez.util.handleApiError
import com.example.rez.util.visible
import kotlinx.coroutines.launch
import javax.inject.Inject


class ComplaintsFragment : Fragment() {


    private var _binding: FragmentComplaintsBinding? = null
    private val binding get() = _binding!!
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
        _binding = FragmentComplaintsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        rezViewModel.complaintResponse.observe(viewLifecycleOwner, Observer {
            binding.submit.progressBar.visible(it is Resource.Loading)
            binding.submit.button.text = "Please wait..."
            when(it) {
                is Resource.Success -> {
                    binding.submit.button.text = "Submit"
                    if (it.value.status){
                        lifecycleScope.launch {
                            val message = it.value.message
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                            val action = ComplaintsFragmentDirections.actionComplaintsFragmentToHome2()
                            findNavController().navigate(action)
                        }
                    } else {
                        val message = it.value.message
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Failure -> {
                    binding.submit.button.text = "Submit"
                    handleApiError(it)
                }
            }
        })
        binding.submit.submit.setOnClickListener {
            if (binding.subject.text!!.isEmpty() || binding.message.text!!.isEmpty()) {
                val message = "All inputs are required"
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            } else {
                binding.submit.submit.enable(true)
                complaint()
            }
        }
    }

    private fun complaint() {
        val  subject = binding.subject.text.toString().trim()
        val message = binding.message.text.toString().trim()

        when {
            subject.isEmpty() -> {
                Toast.makeText(requireContext(), "Enter your subject", Toast.LENGTH_SHORT).show()

            }
            message.isEmpty() -> {
                Toast.makeText(requireContext(), "Enter your message", Toast.LENGTH_SHORT).show()
            }
            else -> {
                    val newUser = ComplaintRequest(
                        subject = subject,
                        message = message
                    )
                    rezViewModel.complaints(newUser, token = "Bearer ${sharedPreferences.getString("token", "token")}")
                }
            }
        }
    }

