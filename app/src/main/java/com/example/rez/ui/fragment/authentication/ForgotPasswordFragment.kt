package com.example.rez.ui.fragment.authentication

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentForgotPasswordBinding
import com.example.rez.model.authentication.request.ForgotPasswordRequest
import com.example.rez.ui.RezViewModel
import com.example.rez.util.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
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
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.submitBtn.button.text = "Submit"

        binding.submitBtn.progressBar.visible(false)

        binding.submitBtn.submit.setOnClickListener {
            forgotPassword()
        }
    }

    private fun forgotPassword() {
        val email = binding.emailTextView.text.toString().trim()
        sharedPreferences.edit().putString("email", email).commit()

        when {
            email.isEmpty() -> {
                Toast.makeText(requireContext(), R.string.all_email_cant_be_empty, Toast.LENGTH_SHORT).show()
            }
            else -> {
                if (validateSignUpFieldsOnTextChange()) {
                    val forgotPasswordUser = ForgotPasswordRequest(
                        email = email
                    )
                    rezViewModel.forgotPassword(forgotPasswordUser)
                    rezViewModel.forgotPasswordResponse.observe(viewLifecycleOwner, Observer {
                        binding.submitBtn.progressBar.visible(it is Resource.Loading)
                        binding.submitBtn.button.text = "Please wait..."
                        when(it) {
                            is Resource.Success -> {
                                binding.submitBtn.button.text = "Submit"
                                lifecycleScope.launch {
                                    //val message = it.value.message
                                    val ref = it.value.data.reference
                                    //Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                                    val action =
                                        ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToResetPasswordFragment(ref)
                                    findNavController().navigate(action)
                                }}
                            is Resource.Error<*> -> {
                                binding.submitBtn.button.text = "Submit"
                                showToast(it.data.toString())
                                rezViewModel.forgotPasswordResponse.removeObservers(viewLifecycleOwner)

                            }
                        }
                    })
                }
            }
        }
    }

    private fun validateSignUpFieldsOnTextChange(): Boolean {
        var isValidated = true

        binding.emailTextView.doOnTextChanged { _, _, _, _ ->
            when {
                binding.emailTextView.text.toString().trim().isEmpty() -> {
                    binding.emailTextView.error =
                        getString(R.string.all_email_cant_be_empty)
                    isValidated = false
                }
//                !ValidationObject.validateEmail(binding.emailTextView.text.toString().trim()) -> {
//                    Toast.makeText(requireContext(), R.string.all_invalid_email, Toast.LENGTH_SHORT).show()
//
//                    isValidated = false
//                }
                else -> {
                    isValidated = true
                }
            }
        }


        return isValidated
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }

}