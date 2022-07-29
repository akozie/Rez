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
import com.example.rez.model.authentication.genresponse.RegistrationErrorData
import com.example.rez.model.authentication.genresponse.RegistrationSuccessData
import com.example.rez.model.authentication.request.FacebookRequest
import com.example.rez.model.authentication.request.RegisterRequest
import com.example.rez.model.authentication.response.CountryCodes
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
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch
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
    private lateinit var phoneNumberText: String

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
        phoneNumberText = binding.initPhoneNumber.text.toString().trim()+binding.edtUserMobile.text.toString().trim()
        val phoneNumber = binding.initPhoneNumber
        val countryCode = arrayListOf("+1", "+20", "+212", "+213", "+216", "+218", "+220", "+221", "+222", "+223", "+224", "+225", "+226", "+227", "+228", "+229", "+230", "+231", "+232", "+233", "+234", "+235", "+236", "+237", "+238", "+239", "+240", "+241", "+242", "+243", "+244", "+245", "+246", "+248", "+249", "+250", "+251", "+252", "+253", "+254", "+255", "+256", "+257", "+258", "+260", "+261", "+262", "+262", "+263", "+264", "+265", "+266", "+267", "+268", "+269", "+27", "+290", "+291", "+297", "+298", "+299", "+30", "+31", "+32", "+33", "+34", "+345", "+350", "+351", "+352", "+353", "+354", "+355", "+356", "+358", "+359", "+36", "+370", "+371", "+372", "+373", "+374", "+375", "+376", "+377", "+378", "+379", "+380", "+381", "+382", "+385", "+386", "+387", "+389", "+39", "+40", "+41", "+420", "+421", "+423", "+43", "+44", "+45", "+46", "+47", "+48", "+49", "+500", "+501", "+502", "+503", "+504", "+505", "+506", "+507", "+508", "+509", "+51", "+52", "+53", "+537", "+54", "+55", "+56", "+57", "+58", "+590", "+591", "+593", "+594", "+595", "+596", "+597", "+598", "+599", "+60", "+61", "+62", "+63", "+64", "+65", "+66", "+670", "+672", "+673", "+674", "+675", "+676", "+677", "+678", "+679", "+680", "+681", "+682", "+683", "+685", "+686", "+687", "+688", "+689", "+690", "+691", "+692", "+7", "+77", "+81", "+82", "+84", "+850", "+852", "+853", "+855", "+856", "+86", "+872", "+880", "+886", "+90", "+91", "+92", "+93", "+94", "+95", "+960", "+961", "+962", "+963", "+964", "+965", "+966", "+967", "+968", "+970", "+971", "+972", "+973", "+974", "+975", "+976", "+977", "+98", "+992", "+993", "+994", "+995", "+996", "+998")
        country = arrayListOf()
        val phoneAdapter = PhoneAdapter(countryCode, requireContext(),
            android.R.layout.simple_dropdown_item_1line)
        phoneNumber.setAdapter(phoneAdapter)


        rezViewModel.phoneNumber.observe(viewLifecycleOwner){
            validatePhoneNumber(it)
        }

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


        binding.signIn.setOnClickListener {
            val action = RegistrationFragmentDirections.actionRegistrationFragmentToLoginFragment()
            findNavController().navigate(action)
        }

        binding.loginTv.progressBar.visible(false)
       // binding.signUpTv.enable(false)

        rezViewModel.registerResp.observe(viewLifecycleOwner, Observer {
            binding.loginTv.progressBar.visible(it is Resource.Loading)
            binding.loginTv.button.text = "Please wait.."
            when(it) {
                is Resource.Success -> {
                    binding.loginTv.button.text = "SIGNUP"
                    if (it.value.body()?.status == true){
                        val data = it.value.body()?.data
                        val uEmail = binding.edtUserEmail.text.toString().trim()
                        val token: String? = data?.token
                        sharedPreferences.edit().putString("token", token).commit() // save user's token
                        sharedPreferences.edit().putString("email", uEmail).commit() // save user's email

                        val message = it.value.body()?.message!!
                        startActivity(
                            Intent(
                                requireContext(),
                                DashboardActivity::class.java
                            )
                        )
                        requireActivity().finish()
                        showToast(message)
                    } else {
                        val message = it.value.body()?.message!!
                        showToast(message)
                    }
                }
                is Resource.Failure -> {
                    binding.loginTv.button.text = "SIGNUP"
                    handleApiError(it)
                    val error = it.value as RegResponse

                    if (error.errors?.phone?.isNotEmpty() == true){
                        val phoneErrors = error.errors.phone[0]
                        showToast(phoneErrors)
                    }else if (error.errors?.email?.isNotEmpty() == true ){
                        val emailErrors = error.errors.email[0]
                        showToast(emailErrors)
                    }
                }
            }
        })


//        rezViewModel.registerResp.observe(viewLifecycleOwner, Observer {
//            binding.loginTv.progressBar.visible(it is Resource.Loading)
//            binding.loginTv.button.text = "Please wait.."
//            when(it) {
//                is Resource.Success -> {
//                    binding.loginTv.button.text = "SIGNUP"
//                    if (it.value.containsKey("data")){
//                        val data = it.value.get("data") as RegistrationSuccessData
//                        val uEmail = binding.edtUserEmail.text.toString().trim()
//                        val token: String? = data.token
//                        sharedPreferences.edit().putString("token", token).commit() // save user's token
//                        sharedPreferences.edit().putString("email", uEmail).commit() // save user's email
//
//                        val message = it.value.get("message") as String
//                        startActivity(
//                            Intent(
//                                requireContext(),
//                                DashboardActivity::class.java
//                            )
//                        )
//                        requireActivity().finish()
//                        showToast(message)
//                    } else {
//                        val message = it.value.get("message") as String
//                        showToast(message)
//                    }
//                }
//                is Resource.Failure -> {
//                    binding.loginTv.button.text = "SIGNUP"
//                    handleApiError(it)
//
//                }
//            }
//        })


//        binding.edtFirstName.addTextChangedListener {
//            val email = binding.edtUserEmail.text.toString().trim()
//            binding.signUpTv.enable(email.isNotEmpty() && it.toString().isNotEmpty())
//        }

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
//        binding.edtUserMobile.doOnTextChanged { charSequence, _, _, _ ->
////            when {
////                binding.edtUserMobile.text.toString().trim().isEmpty() -> {
////                    binding.TILedtMobile.error =
////                        getString(R.string.all_phone_number_is_required)
////                    isValidated = false
////                }
////                !phoneNumberText.isValidPhoneNumber() -> {
////                    binding.TILedtMobile.error =
////                        getString(R.string.invalid_phone_number)
////                    isValidated = false
////                }
////                else -> {
////                    binding.TILedtMobile.error = null
////                    isValidated = true
////                }
////            }
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
                binding.loginTv.progressBar.visible(it is Resource.Loading) // hide progress bar after the response
                binding.loginTv.button.text = "Please wait.."
                when (it) {
                    is Resource.Success -> {
                        binding.loginTv.button.text = "SIGNUP"
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
                    is Resource.Failure ->  {
                        binding.loginTv.button.text = "SIGNUP"
                        handleApiError(it)
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
                    is Resource.Failure ->  {
                        binding.loginTv.button.text = "SIGNUP"
                        handleApiError(it)
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
}