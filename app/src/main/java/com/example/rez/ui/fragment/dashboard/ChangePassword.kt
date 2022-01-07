package com.example.rez.ui.fragment.dashboard

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentChangePasswordBinding
import com.example.rez.model.authentication.request.ChangePasswordRequest
import com.example.rez.ui.RezViewModel
import com.example.rez.ui.activity.DashboardActivity
import com.example.rez.util.enable
import com.example.rez.util.handleApiError
import kotlinx.coroutines.launch
import javax.inject.Inject


class ChangePassword : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var rezViewModel: RezViewModel

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
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rezViewModel = (activity as DashboardActivity).rezViewModel


        rezViewModel.changePasswordResponse.observe(viewLifecycleOwner, Observer {
           // binding.progressBar.visible(it is Resource.Loading)
            when(it) {
                is Resource.Success -> {
                    if (it.value.status){
                        lifecycleScope.launch {
                            val message = it.value.message
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val message = it.value.message
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }

                }
                is Resource.Failure -> handleApiError(it)
            }
        })

        binding.changePassTv.setOnClickListener {
            if (binding.currentPassEt.text!!.isEmpty() || binding.newPassEt.text!!.isEmpty()) {
                val message = "All inputs are required"
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            } else {
                binding.changePassTv.enable(true)
                changePassword()
            }
        }
    }

    private fun changePassword() {
        val  current = binding.currentPassEt.text.toString().trim()
        val new = binding.newPassEt.text.toString().trim()
        val confirm = binding.confNewPassEt.text.toString().trim()

        when {
            current.isEmpty() -> {
                Toast.makeText(requireContext(), R.string.all_email_cant_be_empty, Toast.LENGTH_SHORT).show()

            }
            new.isEmpty() -> {
                Toast.makeText(requireContext(), R.string.all_password_is_required, Toast.LENGTH_SHORT).show()
            }
            else -> {
                if (validateSignUpFieldsOnTextChange()) {
                    val newUser = ChangePasswordRequest(
                        current_password = current,
                        password = new,
                        password_confirmation = confirm)
                    rezViewModel.changePassword(newUser, token = "Bearer ${sharedPreferences.getString("token", "token")}")
                }
            }
        }
    }

    private fun validateSignUpFieldsOnTextChange(): Boolean {
        var isValidated = true


        binding.currentPassEt.doOnTextChanged { _, _, _, _ ->
            when {
                binding.currentPassEt.text.toString().trim().isEmpty() -> {
                    Toast.makeText(requireContext(), R.string.all_password_is_required, Toast.LENGTH_SHORT).show()
                    isValidated = false
                }
                else -> {
                    isValidated = true
                }
            }
        }


        return isValidated
    }

}