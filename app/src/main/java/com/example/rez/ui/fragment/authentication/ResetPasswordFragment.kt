package com.example.rez.ui.fragment.authentication

import android.content.SharedPreferences
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
import androidx.navigation.fragment.navArgs
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentResetPasswordBinding
import com.example.rez.model.authentication.request.ForgotPasswordRequest
import com.example.rez.model.authentication.request.ResPasswordRequest
import com.example.rez.ui.RezViewModel
import com.example.rez.util.ValidationObject
import com.example.rez.util.enable
import com.example.rez.util.handleApiError
import com.example.rez.util.visible
import kotlinx.coroutines.launch
import javax.inject.Inject


class ResetPasswordFragment : Fragment() {

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!
    private val rezViewModel: RezViewModel by activityViewModels()
    val args: ResetPasswordFragmentArgs by navArgs()

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
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBar.visible(false)
        binding.resetSubmitBtn.enable(false)

        rezViewModel.resetPasswordResponse.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)
            when(it) {
                is Resource.Success -> {
                    lifecycleScope.launch {
                        val message = it.value.message
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        val action =
                            ResetPasswordFragmentDirections.actionResetPasswordFragmentToLoginFragment()
                        findNavController().navigate(action)
                    }}
                is Resource.Failure -> handleApiError(it)
            }
        })

        rezViewModel.forgotPasswordResponse.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)
            when(it) {
                is Resource.Success -> {
                    lifecycleScope.launch {
                        val message = it.value.message
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }}
                is Resource.Failure -> handleApiError(it)
            }
        })


        binding.edtPass.addTextChangedListener {
            val email = binding.edtPass.text.toString().trim()
            binding.resetSubmitBtn.enable(email.isNotEmpty() && it.toString().isNotEmpty())
        }

        binding.resetSubmitBtn.setOnClickListener {
            resetPassword()
        }

        binding.resendBtn.setOnClickListener {
            resend()
        }
    }

    private fun resend() {
        val email = sharedPreferences.getString("email", "email")!!

        when {
            email.isEmpty() -> {
                Toast.makeText(requireContext(), R.string.all_email_cant_be_empty, Toast.LENGTH_SHORT).show()
            }
            !ValidationObject.validateEmail(email) -> {
                Toast.makeText(requireContext(), R.string.all_invalid_email, Toast.LENGTH_SHORT).show()

            }
            else -> {
                if (validateSignUpFieldsOnTextChange()) {
                    val forgotPasswordUser = ForgotPasswordRequest(
                        email = email
                    )
                    rezViewModel.forgotPassword(forgotPasswordUser)
                }
            }
        }
    }


    private fun resetPassword() {
        /*Initialize User Inputs*/
        val code =  binding.edtCode.text.toString().trim()
        val password = binding.edtPass.text.toString().trim()
        val confirmPassword = binding.edtConfirmPass.text.toString().trim()
        val ref = args.reference!!

        when {
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
            !ValidationObject.validatePasswordMismatch(password, confirmPassword) -> {
                binding.TILedtConfirmPass.error =
                    getString(R.string.all_password_mismatch)
                binding.TILedtConfirmPass.errorIconDrawable =
                    null
            }
            else -> {
                if (validateSignUpFieldsOnTextChange()) {
                    val newRegisterUser = ResPasswordRequest(
                        code = code,
                        password = password,
                        password_confirmation = confirmPassword,
                        reference = ref
                    )
                    rezViewModel.resetPassword(newRegisterUser)
                }
            }
        }
    }

    private fun validateSignUpFieldsOnTextChange(): Boolean {
        var isValidated = true

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
                !ValidationObject.validatePasswordMismatch(
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