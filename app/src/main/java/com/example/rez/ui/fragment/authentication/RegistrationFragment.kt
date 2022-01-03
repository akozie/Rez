package com.example.rez.ui.fragment.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.rez.R
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentRegistrationBinding
import com.example.rez.model.authentication.request.RegisterRequest
import com.example.rez.ui.RezViewModel
import com.example.rez.util.ValidationObject.validateEmail
import com.example.rez.util.ValidationObject.validatePasswordMismatch
import com.example.rez.util.ValidationObject.validatePhoneNumber
import com.example.rez.util.enable
import com.example.rez.util.handleApiError
import com.example.rez.util.visible
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [RegistrationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegistrationFragment : Fragment() {
    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!
    private val rezViewModel: RezViewModel by activityViewModels()

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

        binding.progressBar.visible(false)
        binding.signUpTv.enable(false)

        rezViewModel.registerResponse.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)
            when(it) {
                is Resource.Success -> {
                    lifecycleScope.launch {
                        val message = it.value.message
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        val action =
                            RegistrationFragmentDirections.actionRegistrationFragmentToOTPFragment()
                        findNavController().navigate(action)
                    }}
                is Resource.Failure -> handleApiError(it) { register() }
            }
        })

        binding.edtFirstName.addTextChangedListener {
            val email = binding.edtUserEmail.text.toString().trim()
            binding.signUpTv.enable(email.isNotEmpty() && it.toString().isNotEmpty())
        }

        binding.signUpTv.setOnClickListener {
            register()
        }

    }

    override fun onResume() {
        super.onResume()
        /*Method to Validate All Sign Up Fields*/
        validateSignUpFieldsOnTextChange()
    }

    private fun register() {


        /*Initialize User Inputs*/
        val firstName = binding.edtFirstName.text.toString().trim()
        val lastName = binding.edtLastName.text.toString().trim()
        val email = binding.edtUserEmail.text.toString().trim()
        val phoneNumber = binding.initPhoneNumber.text.toString().trim() + binding.edtUserMobile.text.toString().trim()
        val password = binding.edtPass.text.toString().trim()
        val confirmPassword = binding.edtConfirmPass.text.toString().trim()

        when {
            firstName.isEmpty() -> {
                binding.TILedtFirstName.error =
                    getString(R.string.all_please_enter_first_name)

            }
            lastName.isEmpty() -> {
                binding.TILedtLastName.error =
                    getString(R.string.all_please_enter_last_name)

            }
            email.isEmpty() -> {
                binding.TILedtEmail.error =
                    getString(R.string.all_email_cant_be_empty)

            }
            phoneNumber.isEmpty() -> {
                binding.TILedtMobile.error =
                    getString(R.string.all_phone_number_is_required)

            }
            !validateEmail(email) -> {
                binding.TILedtMobile.error =
                    getString(R.string.all_invalid_email)

            }
            password.isEmpty() -> {
                binding.TILedtPass.error =
                    getString(R.string.all_password_is_required)
                binding.TILedtPass.errorIconDrawable = null

            }
            password.length <= 6 -> {
                binding.TILedtPass.error =
                    getString(R.string.valid_password_is_required)
                binding.TILedtPass.errorIconDrawable = null

            }
            confirmPassword.isEmpty() -> {
                binding.TILedtConfirmPass.error =
                    getString(R.string.all_password_is_required)
                binding.TILedtConfirmPass.errorIconDrawable =
                    null
            }
            !validatePasswordMismatch(password, confirmPassword) -> {
                binding.TILedtConfirmPass.error =
                    getString(R.string.all_password_mismatch)
                binding.TILedtConfirmPass.errorIconDrawable =
                    null
            }
            else -> {
                if (validateSignUpFieldsOnTextChange()) {
                    val newRegisterUser = RegisterRequest(
                        first_name = firstName,
                        last_name = lastName,
                        email = email,
                        phone = phoneNumber,
                        password = password,
                        password_confirmation = confirmPassword
                    )
                    rezViewModel.register(newRegisterUser)
                }
            }
        }
    }

    private fun validateSignUpFieldsOnTextChange(): Boolean {
        var isValidated = true

        binding.edtFirstName.doOnTextChanged { _, _, _, _ ->
            when {
                binding.edtFirstName.text.toString().trim().isEmpty() -> {
                    binding.TILedtFirstName.error =
                        getString(R.string.all_please_enter_first_name)
                    isValidated = false
                }
                else -> {
                    binding.TILedtFirstName.error = null
                    isValidated = true
                }
            }
        }
        binding.edtLastName.doOnTextChanged { _, _, _, _ ->
            when {
                binding.edtLastName.text.toString().trim().isEmpty() -> {
                    binding.TILedtLastName.error =
                        getString(R.string.all_please_enter_last_name)
                    isValidated = false
                }
                else -> {
                    binding.TILedtLastName.error = null
                    isValidated = true
                }
            }
        }

        binding.edtUserEmail.doOnTextChanged { _, _, _, _ ->
            when {
                binding.edtUserEmail.text.toString().trim().isEmpty() -> {
                    binding.TILedtEmail.error =
                        getString(R.string.all_email_cant_be_empty)
                    isValidated = false
                }
                !validateEmail(binding.edtUserEmail.text.toString().trim()) -> {
                    binding.TILedtEmail.error =
                        getString(R.string.all_invalid_email)
                    isValidated = false
                }
                else -> {
                    binding.TILedtEmail.error = null
                    isValidated = true
                }
            }
        }


        binding.edtUserMobile.doOnTextChanged { _, _, _, _ ->
            when {
                binding.edtUserMobile.text.toString().trim().isEmpty() -> {
                    binding.TILedtMobile.error =
                        getString(R.string.all_phone_number_is_required)
                    isValidated = false
                }
                else -> {
                    binding.TILedtMobile.error = null
                    isValidated = true
                }
            }
        }


        binding.edtPass.doOnTextChanged { _, _, _, _ ->
            when {
                binding.edtPass.text.toString().trim().isEmpty() -> {
                    binding.TILedtPass.error =
                        getString(R.string.all_password_is_required)
                    binding.TILedtPass.errorIconDrawable = null
                    isValidated = false
                }
                else -> {
                    binding.TILedtPass.error = null
                    isValidated = true
                }
            }
        }

        binding.edtConfirmPass.doOnTextChanged { _, _, _, _ ->
            when {
                binding.edtConfirmPass.text.toString().trim().isEmpty() -> {
                    binding.TILedtConfirmPass.error =
                        getString(R.string.all_password_is_required)
                    binding.TILedtConfirmPass.errorIconDrawable =
                        null
                    isValidated = false
                }
                !validatePasswordMismatch(
                    binding.edtPass.text.toString().trim(),
                    binding.edtConfirmPass.text.toString().trim()
                ) -> {
                    binding.TILedtConfirmPass.error =
                        getString(R.string.all_password_mismatch)
                    binding.TILedtConfirmPass.errorIconDrawable =
                        null
                    isValidated = false
                }
                else -> {
                    binding.TILedtConfirmPass.error = null
                    isValidated = true
                }
            }
        }


        return isValidated
    }


}