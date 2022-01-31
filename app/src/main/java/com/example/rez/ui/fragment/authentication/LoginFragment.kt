package com.example.rez.ui.fragment.authentication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentLoginBinding
import com.example.rez.model.authentication.request.LoginRequest
import com.example.rez.ui.RezViewModel
import com.example.rez.ui.activity.DashboardActivity
import com.example.rez.ui.activity.MainActivity
import com.example.rez.util.*
import com.example.rez.util.ValidationObject.validateEmail
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var rezViewModel: RezViewModel
    private lateinit var googleSignInButton: TextView
    private lateinit var rezSignInClient: GoogleSignInClient
    private var GOOGLE_SIGNIN_RQ_CODE = 100

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
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rezViewModel = (activity as MainActivity).rezViewModel
        googleSignInButton = binding.googleTv
        googleSignInClient()

        binding.register.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegistrationFragment()
            findNavController().navigate(action)
        }

        binding.forgotPasswordTv.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
            findNavController().navigate(action)
        }

//        binding.progressBar.visible(false)
//        binding.loginTv.enable(false)

        rezViewModel.loginResponse.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)
            when(it) {
                is Resource.Success -> {
                    if (it.value.status){
                        lifecycleScope.launch {
                            val uEmail = binding.edtUserEmail.text.toString().trim()
                            val token: String? = it.value.data.token
                            val user = sharedPreferences.edit().putString("token", token).commit()
                            sharedPreferences.edit().putString("email", uEmail).commit()
                            Log.i("TOK", user.toString())

                            val message = it.value.message
                            showToast(message)
                            startActivity(
                                Intent(
                                    requireContext(),
                                    DashboardActivity::class.java
                                )
                        )
                            requireActivity().finish()
                        }
                    } else {
                        it.value.message?.let { it1 -> showToast(it1) }

                    }

                }
                is Resource.Failure -> handleApiError(it)
            }
        })

        binding.edtUserEmail.addTextChangedListener {
            val email = binding.edtUserEmail.text.toString().trim()
            binding.loginTv.enable(email.isNotEmpty() && it.toString().isNotEmpty())
        }

        binding.loginTv.setOnClickListener {
            if (binding.edtUserEmail.text!!.isEmpty() || binding.edtPass.text!!.isEmpty()) {
                val message = "All inputs are required"
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            } else {
                binding.loginTv.enable(true)
                login()
            }
        }

        googleSignInButton.setOnClickListener {
            signIn()
        }

    }

    private fun login() {
        val email = binding.edtUserEmail.text.toString().trim()
        val password = binding.edtPass.text.toString().trim()

        when {
            email.isEmpty() -> {
                binding.TILedtUserEmail.error =
                    getString(R.string.all_email_cant_be_empty)

            }
            !validateEmail(email) -> {
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
                        email = email,
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

    /*create the googleSignIn client*/
    private fun googleSignInClient() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
           // .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        rezSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions)
    }

    /*launch the signIn with google dialog*/
    private fun signIn() {
        rezSignInClient.signOut()
        val signInIntent = rezSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGNIN_RQ_CODE)
    }

    /*gets the selected google account from the intent*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGNIN_RQ_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    /*handles the result of successful sign in*/
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            loadDashBoardFragment(account)
        } catch (e: ApiException) {
            showToast(e.localizedMessage)
        }
    }

    /*open the dashboard fragment if account was selected*/
    private fun loadDashBoardFragment(account: GoogleSignInAccount?) {
        if (account != null) {

            account.idToken.let {
                if (it != null) {
                    sharedPreferences.edit().putString("googletoken", it).commit()                }
            }

           // authenticationViewModel.loginUserWithGoogle(null)

            binding.progressBar.visibility = View.VISIBLE

            /*Handling the response from the retrofit*/
//            authenticationViewModel.loginUserWithGoogle.observe(
//                viewLifecycleOwner,
//                Observer {
//                    when (it) {
//                        is Resource.Success -> {
//                            val successResponse = it.data?.payload
//                            if (successResponse != null) {
//                                sessionManager.saveToSharedPref(TOKEN, successResponse)
//                            }
//
//                            progressDialog.hideProgressDialog()
//                            val intent = Intent(requireContext(), DashboardActivity::class.java)
//                            startActivity(intent)
//                            activity?.finish()
//                        }
//
//                        is Resource.Error -> {
//                            progressDialog.hideProgressDialog()
//                            handleApiError(it, mainRetrofit, requireView(), sessionManager, database)
//                        }
//                        is Resource.Loading -> {
//                            progressDialog.showDialogFragment("Logging In...")
//                        }
//                    }
//                }
//            )
        }
    }


}