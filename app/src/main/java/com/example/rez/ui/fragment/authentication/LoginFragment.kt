package com.example.rez.ui.fragment.authentication

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.rez.R
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentLoginBinding
import com.example.rez.model.authentication.request.LoginRequest
import com.example.rez.ui.RezViewModel
import com.example.rez.ui.activity.DashboardActivity
import com.example.rez.util.ValidationObject.validateEmail
import com.example.rez.util.enable
import com.example.rez.util.handleApiError
import com.example.rez.util.snackbar
import com.example.rez.util.visible
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val rezViewModel: RezViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.register.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegistrationFragment()
            findNavController().navigate(action)
        }

        binding.forgotPasswordTv.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
            findNavController().navigate(action)
        }

        binding.progressBar.visible(false)
        binding.loginTv.enable(true)

        rezViewModel.loginResponse.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)
            when(it) {
                is Resource.Success -> {
                    if (it.value.status){
                        lifecycleScope.launch {
                           // userPreferences.saveAuthToken(it.value.user?.userID.toString())
                            //val userId = it.value.user?.userID
                            //val userEmailID = it.value.user?.email
                            Toast.makeText(requireContext(), "LOGIN SUCCESSFUL", Toast.LENGTH_SHORT).show()
                            startActivity(
                                Intent(
                                    requireContext(),
                                    DashboardActivity::class.java
                                )
//                                apply {
//                                    with(this) {
//                                        putExtra("USER_ID", userId)
//                                        putExtra("USER_EMAIL", userEmailID)
//                                    }
//                                }
                        )
                            requireActivity().finish()
                        }
                    } else {
                        it.value.message?.let { it1 -> snackbar(it1) }
                    }

                }
                is Resource.Failure -> handleApiError(it) { login() }
            }
        })

        binding.edtUserEmail.addTextChangedListener {
            val email = binding.edtUserEmail.text.toString().trim()
            binding.edtUserEmail.enable(email.isNotEmpty() && it.toString().isNotEmpty())
        }

        binding.loginTv.setOnClickListener {
            if (binding.edtUserEmail.text?.isEmpty() == true || binding.edtPass.text?.isEmpty() == true) {
                val message = "All inputs are required"
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            } else {
                binding.loginTv.enable(true)
                login()
            }
        }
    }

    private fun login() {
        val emailAddress = binding.edtUserEmail.text.toString().trim()
        val password = binding.edtPass.text.toString().trim()

        when {
            emailAddress.isEmpty() -> {
                binding.TILedtPass.error =
                    getString(R.string.all_email_cant_be_empty)
            }

            !validateEmail(emailAddress) -> {
                binding.TILedtUserEmail.error =
                    getString(R.string.all_invalid_email)

            }
            password.isEmpty() -> {
                binding.TILedtPass.error =
                    getString(R.string.all_password_is_required)
                binding.TILedtPass.errorIconDrawable = null

            }
            else -> {
                if (validateSignUpFieldsOnTextChange()) {
                    val newUser = LoginRequest(
                        email = emailAddress,
                        password = password)
                    rezViewModel.login(newUser)
                }
            }
        }
    }

    private fun validateSignUpFieldsOnTextChange(): Boolean {
        var isValidated = true

        binding.edtUserEmail.doOnTextChanged { _, _, _, _ ->
            when {
                binding.edtUserEmail.text.toString().trim().isEmpty() -> {
                    binding.TILedtUserEmail.error =
                        getString(R.string.all_email_cant_be_empty)
                    isValidated = false
                }
                !validateEmail(binding.edtUserEmail.text.toString().trim()) -> {
                    binding.TILedtUserEmail.error =
                        getString(R.string.all_invalid_email)
                    isValidated = false
                }
                else -> {
                    binding.TILedtUserEmail.error = null
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


        return isValidated
    }


}