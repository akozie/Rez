package com.example.rez.ui.fragment.authentication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.adapter.PhoneAdapter
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentRegistrationBinding
import com.example.rez.model.authentication.genresponse.RegResponse
import com.example.rez.model.authentication.request.FacebookRequest
import com.example.rez.model.authentication.request.RegisterRequest
import com.example.rez.model.authentication.response.CountryCodes
import com.example.rez.model.dashboard.FcmTokenRequest
import com.example.rez.ui.RezViewModel
import com.example.rez.ui.activity.DashboardActivity
import com.example.rez.util.ValidationObject.isValidPhoneNumber
import com.example.rez.util.ValidationObject.validateEmail
import com.example.rez.util.ValidationObject.validatePasswordMismatch
import com.example.rez.util.ValidationObject.validatePhoneNumber
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
import com.google.android.gms.common.api.Response
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class RegistrationFragment : Fragment() {
    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!
    private val rezViewModel: RezViewModel by activityViewModels()
    private lateinit var googleSignInButton: TextView
    private lateinit var facebookSignInButton: LoginButton
    private lateinit var rezSignInClient: GoogleSignInClient
    private var GOOGLE_SIGNIN_RQ_CODE = 100
    private lateinit var callbackManager : CallbackManager
    private lateinit var fcmToken: String

 //   private lateinit var phoneNumber: AutoCompleteTextView
    private lateinit var country: ArrayList<CountryCodes>
    //private lateinit var phone: String
   // private lateinit var phoneAdapter: ArrayAdapter


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
        binding.loginTv.button.text = "SIGNUP"

        FirebaseMessaging.getInstance().token.addOnCompleteListener { token ->
            if (!token.isSuccessful) {
                //return@addOnCompleteListener
            }
             fcmToken = token.result //this is the token retrieved
        }
        rezViewModel.phoneNumber.observe(viewLifecycleOwner){
            validatePhoneNumber(it)
        }

        googleSignInButton = binding.googleTv
        facebookSignInButton  = binding.facebookTv
        googleSignInClient()

        googleSignInButton.setOnClickListener {
            signIn()
        }

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

        binding.signIn.setOnClickListener {
            val action = RegistrationFragmentDirections.actionRegistrationFragmentToLoginFragment()
            findNavController().navigate(action)
        }

        binding.loginTv.progressBar.visible(false)
       // binding.signUpTv.enable(false)



        binding.loginTv.submit.setOnClickListener {
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
                binding.TILedtMobile.errorIconDrawable = null
            }
//            !phoneNumber.isValidPhoneNumber() -> {
//                binding.TILedtMobile.error =
//                    getString(R.string.invalid_phone_number)
//                binding.TILedtMobile.errorIconDrawable =
//                    null
//            }
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
                        fcm_token = fcmToken,
                        phone = phoneNumber,
                        password = password,
                        password_confirmation = confirmPassword
                    )
                    rezViewModel.register(newRegisterUser)
                    rezViewModel.regResponse.observe(viewLifecycleOwner, Observer {
                        binding.loginTv.progressBar.visible(it is Resource.Loading)
                        binding.loginTv.button.text = "Please wait.."
                        when(it) {
                            is Resource.Success -> {
                                binding.loginTv.button.text = "SIGNUP"
                                if (it.value.status){
                                    val data = it.value.data
                                    val uEmail = binding.edtUserEmail.text.toString().trim()
                                    val token: String = data.token
                                    val uName: String = data.first_name
                                    sharedPreferences.edit().putString("name", uName).commit()
                                    sharedPreferences.edit().putString("token", token).commit() // save user's token
                                    sharedPreferences.edit().putString("email", uEmail).commit() // save user's email
                                    val message = it.value.message
                                    showToast(message)
                                    startActivity(
                                        Intent(
                                            requireContext(),
                                            DashboardActivity::class.java
                                        )
                                    )
                                    requireActivity().finish()
                                } else {
                                    val message = it.value.message
                                    showToast(message)
                                }
                            }
                            is Resource.Error<*> -> {
                                binding.loginTv.button.text = "SIGNUP"
                                showToast(it.data.toString())
                                rezViewModel.regResponse.removeObservers(viewLifecycleOwner)
                            }
                        }
                    })
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
//                !validateEmail(binding.edtUserEmail.text.toString().trim()) -> {
//                    binding.TILedtEmail.error =
//                        getString(R.string.all_invalid_email)
//                    isValidated = false
//                }
                else -> {
                    binding.TILedtEmail.error = null
                    isValidated = true
                }
            }
        }
//        binding.edtUserMobile.doOnTextChanged { charSequence, _, _, _ ->
//            when {
//                binding.edtUserMobile.text.toString().trim().isEmpty() -> {
//                    binding.TILedtMobile.error =
//                        getString(R.string.all_phone_number_is_required)
//                    isValidated = false
//                }
//                !phoneNumberText.isValidPhoneNumber() -> {
//                    binding.TILedtMobile.error =
//                        getString(R.string.invalid_phone_number)
//                    isValidated = false
//                }
//                else -> {
//                    binding.TILedtMobile.error = null
//                    isValidated = true
//                }
//            }
//            rezViewModel.updatePhoneNumber(charSequence.toString())
//        }

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
                binding.loginTv.button.text = "Please wait.."
                when (it) {
                    is Resource.Success -> {
                        binding.loginTv.button.text = "SIGNUP"
                        if (it.value.status) {
                            lifecycleScope.launch {
                                val uEmail = it.value.data.email
                                val token: String? = it.value.data.token
                                val uName: String? = it.value.data.first_name
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
                        binding.loginTv.button.text = "SIGNUP"
                        showToast(it.data.toString())
                        rezViewModel.loginGoogleResponse.removeObservers(viewLifecycleOwner)
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
                binding.loginTv.button.text = "Please wait.."
                when (it) {
                    is Resource.Success -> {
                        binding.loginTv.button.text = "SIGNUP"
                        if (it.value.status) {
                            lifecycleScope.launch {
                                val uEmail = it.value.data.email
                                val token: String? = it.value.data.token
                                val uName: String? = it.value.data.first_name
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
                        binding.loginTv.button.text = "SIGNUP"
                        showToast(it.data.toString())
                        rezViewModel.loginWithFacebookResponse.removeObservers(viewLifecycleOwner)
                    } // handle error
                }
            })
        }
    }

    private fun validatePhoneNumber(phoneNumber:String){
        when{
            !phoneNumber.isValidPhoneNumber() ->{
                binding.TILedtMobile.error =
                        getString(R.string.invalid_phone_number)
            }
        }
    }



//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}