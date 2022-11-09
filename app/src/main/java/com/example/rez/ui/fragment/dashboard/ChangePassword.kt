package com.example.rez.ui.fragment.dashboard

import android.content.Intent
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
import androidx.navigation.fragment.findNavController
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentChangePasswordBinding
import com.example.rez.model.authentication.request.ChangePasswordRequest
import com.example.rez.ui.RezViewModel
import com.example.rez.ui.activity.DashboardActivity
import com.example.rez.ui.activity.MainActivity
import com.example.rez.util.*
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

        binding.changePassTv.button.text = "Change Password"
        binding.changePassTv.submit.setOnClickListener {
            if (binding.currentPassEt.text!!.isEmpty() || binding.newPassEt.text!!.isEmpty()) {
                val message = "All inputs are required"
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            } else {
                binding.changePassTv.submit.enable(true)
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
                    rezViewModel.changePasswordResponse.observe(viewLifecycleOwner, Observer {
                        binding.changePassTv.progressBar.visible(it is Resource.Loading)
                        binding.changePassTv.button.text = "Please wait..."
                        when(it) {
                            is Resource.Success -> {
                                binding.changePassTv.button.text = "Change Password"
                                if (it.value.status){
                                    lifecycleScope.launch {
                                        val message = it.value.message
                                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                                        val action = ChangePasswordDirections.actionChangePasswordToHome2()
                                        findNavController().navigate(action)
                                    }
                                } else {
                                    val message = it.value.message
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                                    val action = ChangePasswordDirections.actionChangePasswordToSettings2()
                                    findNavController().navigate(action)
                                }
                            }
                            is Resource.Error<*> -> {
                                binding.changePassTv.button.text = "Change Password"
                                showToast(it.data.toString())
                                rezViewModel.changePasswordResponse.removeObservers(viewLifecycleOwner)
                            }
                        }
                    })

                }
            }
        }
    }

    private fun validateSignUpFieldsOnTextChange(): Boolean {
        var isValidated = true
        binding.currentPassEt.doOnTextChanged { _, _, _, _ ->
            when {
                binding.currentPassEt.text.toString().trim().isEmpty() ->
                {
                    Toast.makeText(requireContext(), R.string.all_password_is_required, Toast.LENGTH_SHORT).show()
                    isValidated = false
                }
                else -> {
                    isValidated = true
                }
            }
        }
        binding.newPassEt.doOnTextChanged { _, _, _, _ ->
            when {
                binding.newPassEt.text.toString().trim().isEmpty() -> {
                    binding.newTILed.error =
                        getString(R.string.all_password_is_required)
                    binding.newTILed.errorIconDrawable = null
                    isValidated = false
                }
                else -> {
                    binding.newTILed.error = null
                    isValidated = true
                }
            }
        }

        binding.confNewPassEt.doOnTextChanged { _, _, _, _ ->
            when {
                binding.confNewPassEt.text.toString().trim().isEmpty() -> {
                    binding.confirmNewTILedtPass.error =
                        getString(R.string.all_password_is_required)
                    binding.confirmNewTILedtPass.errorIconDrawable =
                        null
                    isValidated = false
                }
                !ValidationObject.validatePasswordMismatch(
                    binding.newPassEt.text.toString().trim(),
                    binding.confNewPassEt.text.toString().trim()
                ) -> {
                    binding.confirmNewTILedtPass.error =
                        getString(R.string.all_password_mismatch)
                    binding.confirmNewTILedtPass.errorIconDrawable =
                        null
                    isValidated = false
                }
                else -> {
                    binding.confirmNewTILedtPass.error = null
                    isValidated = true
                }
            }
        }       
        return isValidated
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}