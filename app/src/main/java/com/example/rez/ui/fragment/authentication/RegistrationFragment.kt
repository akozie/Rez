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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentRegistrationBinding
import com.example.rez.model.authentication.request.FacebookRequest
import com.example.rez.model.authentication.request.RegisterRequest
import com.example.rez.ui.RezViewModel
import com.example.rez.ui.activity.DashboardActivity
import com.example.rez.util.ValidationObject.validateEmail
import com.example.rez.util.ValidationObject.validatePasswordMismatch
import com.example.rez.util.enable
import com.example.rez.util.handleApiError
import com.example.rez.util.showToast
import com.example.rez.util.visible
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


class RegistrationFragment : Fragment() {
    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!
    private val rezViewModel: RezViewModel by activityViewModels()
    private lateinit var googleSignInButton: TextView
    private lateinit var facebookSignInButton: LoginButton
    private lateinit var rezSignInClient: GoogleSignInClient
    private var GOOGLE_SIGNIN_RQ_CODE = 100
    private lateinit var callbackManager : CallbackManager

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
        _binding =  FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        googleSignInButton = binding.googleTv
        facebookSignInButton  = binding.facebookTv
        googleSignInClient()

        googleSignInButton.setOnClickListener {
            signIn()
        }

        facebookSignInButton.setPermissions(Arrays.asList("email"))
        facebookSignInButton.setFragment(this)
        callbackManager = CallbackManager.Factory.create()


        facebookSignInButton.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                Log.d("MainActivity", "Facebook token: " + loginResult!!.accessToken.token)
                // App code
                startDashboardFromFacebook()
            }
            override fun onCancel() { // App code
            }
            override fun onError(exception: FacebookException) { // App code
            }
        })


        binding.signUpTv.setOnClickListener {
            val action = RegistrationFragmentDirections.actionRegistrationFragmentToOTPFragment()
            findNavController().navigate(action)
        }
        binding.signIn.setOnClickListener {
            val action = RegistrationFragmentDirections.actionRegistrationFragmentToLoginFragment()
            findNavController().navigate(action)
        }

        binding.progressBar.visible(false)
       // binding.signUpTv.enable(false)

        rezViewModel.registerResponse.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)
            when(it) {
                is Resource.Success -> {
                    lifecycleScope.launch {
                        val uEmail = binding.edtUserEmail.text.toString().trim()
                        val token: String? = it.value.data.token
                        sharedPreferences.edit().putString("token", token).commit() // save user's token
                        sharedPreferences.edit().putString("email", uEmail).commit() // save user's email

                        val message = it.value.message
                        startActivity(
                            Intent(
                                requireContext(),
                                DashboardActivity::class.java
                            )
                        )
                        requireActivity().finish()
                        showToast(message)
                    }
                }
                is Resource.Failure -> handleApiError(it)
            }
        })

//        binding.edtFirstName.addTextChangedListener {
//            val email = binding.edtUserEmail.text.toString().trim()
//            binding.signUpTv.enable(email.isNotEmpty() && it.toString().isNotEmpty())
//        }

        binding.signUpTv.setOnClickListener {
            register() // register user
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
//            !validateEmail(email) -> {
//                binding.TILedtMobile.error =
//                    getString(R.string.all_invalid_email)
//
//            }
            password.isEmpty() -> {
                binding.TILedtPass.error =
                    getString(R.string.all_password_is_required)
                binding.TILedtPass.errorIconDrawable = null

            }
            password.length <= 5 -> {
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

    /*create the googleSignIn client*/
    private fun googleSignInClient() {
        val serverClientId = getString(R.string.default_web_client_id) // get the client id
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(serverClientId)
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
        callbackManager.onActivityResult(requestCode, resultCode, data)

    }

    /*handles the result of successful sign in*/
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            startDashboard(account)
        } catch (e: ApiException) {
            //showToast(e.localizedMessage)
        }
    }

    /*open the dashboard fragment if account was selected*/
    private fun startDashboard(account: GoogleSignInAccount?) {
        account?.idToken?.let {
            val googleRequest = FacebookRequest(it)
            rezViewModel.loginWithGoogle(googleRequest) // make the google login request
            rezViewModel.loginGoogleResponse.observe(viewLifecycleOwner, Observer {
                binding.progressBar.visible(it is Resource.Loading) // hide progress bar after the response
                when (it) {
                    is Resource.Success -> {
                        if (it.value.status) {
                            lifecycleScope.launch {
                                val uEmail = it.value.data.email
                                val token: String? = it.value.data.token
                                sharedPreferences.edit().putString("token", token).commit()
                                sharedPreferences.edit().putString("email", uEmail).commit() //save the user's email
                                startActivity(
                                    Intent(
                                        requireContext(),
                                        DashboardActivity::class.java
                                    )
                                ) // start the dashboard
                                requireActivity().finish() // finish the activity
                                val message = it.value.message
                                showToast(message) // show the user the message

                            }
                        } else {
                            it.value.message?.let { it1 -> showToast(it1) } // show the user the message
                        }

                    }
                    is Resource.Failure ->  handleApiError(it) // handle error
                }
            })
        }
    }

    /*open the dashboard fragment if facebook account was selected*/
    private fun startDashboardFromFacebook() {
        val accessToken = AccessToken.getCurrentAccessToken()
        if (accessToken != null && !accessToken.isExpired){
            val facebookRequest = FacebookRequest(accessToken.token)
            rezViewModel.loginWithFacebook(facebookRequest) // make the facebook login request
            rezViewModel.loginWithFacebookResponse.observe(viewLifecycleOwner, Observer {
                binding.progressBar.visible(it is Resource.Loading) // hide progress bar after the response
                when (it) {
                    is Resource.Success -> {
                        if (it.value.status) {
                            lifecycleScope.launch {
                                val uEmail = it.value.data.email
                                val token: String? = it.value.data.token
                                sharedPreferences.edit().putString("token", token).commit()
                                sharedPreferences.edit().putString("email", uEmail).commit() //save the user's email
                                startActivity(
                                    Intent(
                                        requireContext(),
                                        DashboardActivity::class.java
                                    )
                                ) // start the dashboard
                                requireActivity().finish() // finish the activity
                                val message = it.value.message
                                showToast(message) // show the user the message

                            }
                        } else {
                            it.value.message?.let { it1 -> showToast(it1) } // show the user the message
                        }
                    }
                    is Resource.Failure ->  handleApiError(it) // handle error
                }
            })
        }
    }
}