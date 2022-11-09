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
import co.paystack.android.PaystackSdk.applicationContext
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentLoginBinding
import com.example.rez.model.authentication.request.FacebookRequest
import com.example.rez.model.authentication.request.LoginRequest
import com.example.rez.model.authentication.response.LoginResponse
import com.example.rez.ui.RezViewModel
import com.example.rez.ui.activity.DashboardActivity
import com.example.rez.ui.activity.MainActivity
import com.example.rez.util.*
import com.example.rez.util.ValidationObject.validateEmail
import com.facebook.*
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
import com.facebook.GraphResponse

import com.facebook.GraphRequest

import com.facebook.AccessToken
import org.json.JSONObject


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var rezViewModel: RezViewModel
    private lateinit var googleSignInButton: TextView
    private lateinit var facebookSignInButton: LoginButton
    private lateinit var rezSignInClient: GoogleSignInClient
    private var GOOGLE_SIGNIN_RQ_CODE = 100
    private lateinit var callbackManager : CallbackManager
    private val EMAIL = "public_profile"
    private lateinit var request: GraphRequest

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

        binding.loginTv.button.text = "LOGIN"

        googleSignInButton = binding.googleTv
        facebookSignInButton  = binding.facebookTv
        googleSignInClient()

        //facebookSignInButton.setLoginBehavior(SessionLo.SUPPRESS_SSO);
        facebookSignInButton.setReadPermissions(Arrays.asList("email"))
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

        binding.signupLayout.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegistrationFragment()
            findNavController().navigate(action)
        }

        binding.forgotPasswordTv.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
            findNavController().navigate(action)
        }

        binding.edtUserEmail.addTextChangedListener {
            val email = binding.edtUserEmail.text.toString().trim()
            binding.loginTv.submit.enable(email.isNotEmpty() && it.toString().isNotEmpty())
        }

        binding.loginTv.submit.setOnClickListener {
            if (binding.edtUserEmail.text!!.isEmpty() || binding.edtPass.text!!.isEmpty()) {
                val message = "All inputs are required"
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            } else {
                binding.loginTv.submit.enable(true)
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
//            !validateEmail(email) -> {
//                binding.TILedtUserEmail.error =
//                    getString(R.string.all_invalid_email)
//
//            }
            password.isEmpty() -> {
                binding.TILedtPass.error =
                    getString(R.string.all_password_is_required)
                binding.TILedtPass.errorIconDrawable = null
            }
            else -> {
                if (validateSignUpFieldsOnTextChange()) {
                    val newUser = LoginRequest(
                        email = email,
                        password = password
                    )
                    rezViewModel.login(newUser)
                    rezViewModel.loginResponse.observe(viewLifecycleOwner, Observer {
                        binding.loginTv.progressBar.visible(it is Resource.Loading)
                        binding.loginTv.button.text = "Please wait..."
                        when (it) {
                            is Resource.Success -> {
                                binding.loginTv.button.text = "LOGIN"
                                if (it.value.status) {
                                    lifecycleScope.launch {
                                        val uName = it.value.data.first_name
                                        val uEmail = binding.edtUserEmail.text.toString().trim()
                                        val token: String? = it.value.data.token
                                        val user = sharedPreferences.edit().putString("token", token).commit()
                                        sharedPreferences.edit().putString("email", uEmail).commit()
                                        sharedPreferences.edit().putString("name", uName).commit()
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
                            is Resource.Error<*> -> {
                                binding.loginTv.button.text = "LOGIN"
                                showToast(it.data.toString())
                                rezViewModel.loginResponse.removeObservers(viewLifecycleOwner)
                            }
                        }
                    })

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
        val serverClientId = getString(R.string.default_web_id) // get the client id
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
        account?.idToken?.let { it ->
            val googleRequest = FacebookRequest(it)
            rezViewModel.loginWithGoogle(googleRequest) // make the google login request
            rezViewModel.loginGoogleResponse.observe(viewLifecycleOwner, Observer {
                binding.loginTv.progressBar.visible(it is Resource.Loading) // hide progress bar after the response
                binding.loginTv.button.text = "Please wait..."
                when (it) {
                    is Resource.Success -> {
                        binding.loginTv.button.text = "LOGIN"
                        if (it.value.status) {
                            lifecycleScope.launch {
                                val uName = it.value.data.first_name
                                val uEmail = it.value.data.email
                                val token: String? = it.value.data.token
                                sharedPreferences.edit().putString("name", uName).commit()
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
                    is Resource.Error<*> ->  {
                        binding.loginTv.button.text = "LOGIN"
                        showToast(it.data.toString())
                        rezViewModel.loginGoogleResponse.removeObservers(viewLifecycleOwner)
                        //showToast("User already has an account mapped without a social provider")
                    } // handle error
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
                binding.loginTv.progressBar.visible(it is Resource.Loading) // hide progress bar after the response
                binding.loginTv.button.text = "Please wait..."
                when (it) {
                    is Resource.Success -> {
                        binding.loginTv.button.text = "LOGIN"
                        if (it.value.status) {
                            lifecycleScope.launch {
                                val uName = it.value.data.first_name
                                val uEmail = it.value.data.email
                                val token: String? = it.value.data.token
                                sharedPreferences.edit().putString("name", uName).commit()
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
                    is Resource.Error<*> ->  {
                        binding.loginTv.button.text = "LOGIN"
                        showToast(it.data.toString())
                        rezViewModel.loginWithFacebookResponse.removeObservers(viewLifecycleOwner)
                        //showToast("User already has an account mapped without a social provider")
                    } // handle error
                }
            })
        }
    }



//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}