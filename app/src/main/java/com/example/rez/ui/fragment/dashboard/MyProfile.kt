package com.example.rez.ui.fragment.dashboard

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentMyProfileBinding
import com.example.rez.model.authentication.request.UpdateProfileRequest
import com.example.rez.ui.RezViewModel
import com.example.rez.ui.activity.DashboardActivity
import com.example.rez.ui.fragment.ProfileManagementDialogFragments
import com.example.rez.ui.fragment.ProfileManagementDialogFragments.Companion.createProfileDialogFragment
import com.example.rez.util.*
import com.theartofdev.edmodo.cropper.CropImage
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import javax.inject.Inject

class MyProfile : Fragment() {

    private var _binding: FragmentMyProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var rezViewModel: RezViewModel
    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>


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
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rezViewModel = (activity as DashboardActivity).rezViewModel
        rezViewModel.getProfile(token = "Bearer ${sharedPreferences.getString("token", "token")}")

        /*Dialog fragment functions*/
        accountFirstNameEditDialog()
        accountLastNameDialogFragment()
        accountEmailEditDialog()
        accountPhoneEditDialog()


        /*Initialize Image Cropper*/
        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) {
            it?.let { uri ->
                binding.customerImageIv.imageAlpha = 140
                binding.customerImageIv.setImageURI(uri)
                uploadImageToServer(uri)
            }
        }
        /*Select profile image*/
        binding.imageSelectIv.setOnClickListener {
            Manifest.permission.READ_EXTERNAL_STORAGE.checkForPermission(NAME, READ_IMAGE_STORAGE)
        }

        binding.progressBar.visible(false)
       // binding.saveBtn.enable(false)

        rezViewModel.updateProfileResponse.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)
            when(it) {
                is Resource.Success -> {
                    if (it.value.status){
                        lifecycleScope.launch {
                            val message = it.value.message
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        it.value.message.let { it1 ->
                            Toast.makeText(requireContext(), it1, Toast.LENGTH_SHORT).show() }
                    }
                }
                is Resource.Failure -> handleApiError(it)
            }
        })

        binding.saveBtn.setOnClickListener {
            if (binding.firstNameEt.text!!.isEmpty()) {
                val message = "All inputs are required"
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            } else {
                binding.saveBtn.enable(true)
                updateProfile()
            }
        }


        //GET USER PROFILE
        rezViewModel.getProfileResponse.observe(viewLifecycleOwner, Observer {
            //  binding.progressBar.visible(it is Resource.Loading)
            when(it) {
                is Resource.Success -> {
                    if (it.value.status){
                        lifecycleScope.launch {
                            val message = it.value.message
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                            binding.firstNameEt.text = it.value.data.first_name
                            binding.lastNameEt.text = it.value.data.last_name
                            binding.userEmailEt.text = sharedPreferences.getString("email", "email")
                            binding.mobileNoEt.text = it.value.data.phone.substring(4)
                        }
                    } else {
                        it.value.message?.let { it1 ->
                            Toast.makeText(requireContext(), it1, Toast.LENGTH_SHORT).show() }
                    }
                }
                is Resource.Failure -> handleApiError(it)
            }
        })

    }

  //  private fun uploadImage(user: UploadImageRequest, token: String) {
       // rezViewModel.uploadImage(user, token )
   // }



    private fun updateProfile() {
        val firstName = binding.firstNameEt.text.toString().trim()
        val lastName = binding.lastNameEt.text.toString().trim()
        val email = binding.userEmailEt.text.toString().trim()
        val phoneNumber = binding.phoneText.text.toString().trim() + binding.mobileNoEt.text.toString().trim()

        when {
            email.isEmpty() -> {
                Toast.makeText(requireContext(), R.string.all_email_cant_be_empty, Toast.LENGTH_SHORT).show()

            }
            !ValidationObject.validateEmail(email) -> {
                Toast.makeText(requireContext(), R.string.all_invalid_email, Toast.LENGTH_SHORT).show()

            }
            firstName.isEmpty() -> {
                Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
            }
            else -> {
                    val newUser = UpdateProfileRequest(
                        email = email,
                        first_name = firstName,
                        last_name = lastName,
                        phone = phoneNumber
                    )
              //  val tok = sharedPreferences.getString("token", "token").toString()

                rezViewModel.updateProfile(newUser, token = "Bearer ${sharedPreferences.getString("token", "token")}")
            }
        }
    }

    /*Check for Gallery Permission*/
    private fun String.checkForPermission(name: String, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    this
                ) == PackageManager.PERMISSION_GRANTED -> {
                    cropActivityResultLauncher.launch(null)
                }
                shouldShowRequestPermissionRationale(this) -> showDialog(this, name, requestCode)
                else -> ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(this),
                    requestCode
                )
            }
        }
    }

    // check for permission and make call
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when (requestCode) {

            READ_IMAGE_STORAGE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showToast("permission refused")
                } else {
                    showToast("Permission granted")
                    cropActivityResultLauncher.launch(null)
                }
            }
        }
    }

    // Show dialog for permission dialog
    private fun showDialog(permission: String, name: String, requestCode: Int) {
        // Alert dialog box
        val builder = AlertDialog.Builder(requireContext())
        builder.apply {
            // setting alert properties
            setMessage(getString(R.string.permision_to_access) + name + getString(R.string.is_required_to_use_this_app))
            setTitle("Permission required")
            setPositiveButton("Ok") { _, _ ->
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(permission),
                    requestCode
                )
            }
        }
        val dialog = builder.create()
        dialog.show()
    }


    /*function to crop picture*/
    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setCropMenuCropButtonTitle("Done")
                .setAspectRatio(1, 1)
                .getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            var imageUri: Uri? = null
            try {
                imageUri = CropImage.getActivityResult(intent).uri
            } catch (e: Exception) {
                Timber.d(e.localizedMessage)
            }
            return imageUri
        }
    }

    /*Upload Profile Picture*/
    private fun uploadImageToServer(uri: Uri) {

        // create RequestBody instance from file
        val convertedImageUriToBitmap = uriToBitmap(uri)
        val bitmapToFile = saveBitmap(convertedImageUriToBitmap)

        /*Compress Image then Upload Image*/
        lifecycleScope.launch {
            val compressedImage = Compressor.compress(requireContext(), bitmapToFile!!)
            val imageBody = compressedImage.asRequestBody("image/jpg".toMediaTypeOrNull())
            val image = MultipartBody.Part.createFormData("file", bitmapToFile?.name, imageBody!!)
            rezViewModel.uploadImage(image, token = "Bearer ${sharedPreferences.getString("token", "token")}")
        }

        /*Handling the response from the retrofit*/
        rezViewModel.uploadImageResponse.observe(
            viewLifecycleOwner, Observer {
                when(it) {
                    is Resource.Success -> {
                        if (it.value.status){
                            lifecycleScope.launch {
                                val message = it.value.message
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                                Glide.with(requireContext()).load(it).into(binding.customerImageIv)
                            }
                        } else {
                            it.value.message?.let { it1 ->
                                Toast.makeText(requireContext(), it1, Toast.LENGTH_SHORT).show() }
                        }
                    }
                    is Resource.Failure -> handleApiError(it)
                }
            }
        )
    }

    // firstName Dialog
    private fun accountFirstNameEditDialog() {
        // when first name value is clicked
        childFragmentManager.setFragmentResultListener(
            ACCOUNT_FIRST_NAME_REQUEST_KEY,
            requireActivity()
        ) { key, bundle ->
            // collect input values from dialog fragment and update the firstname text of user
            val firstName = bundle.getString(ACCOUNT_FIRST_NAME_BUNDLE_KEY)
            binding.firstNameEt.text = firstName
        }

        // when first name value is clicked
        binding.firstNameEt.setOnClickListener {
            val currentFirstName = binding.firstNameEt.text.toString()
            val bundle = bundleOf(CURRENT_ACCOUNT_FIRST_NAME_BUNDLE_KEY to currentFirstName)
            createProfileDialogFragment(
                R.layout.account_first_name_dialog_fragment,
                bundle
            ).show(
                childFragmentManager, getString(R.string.frstname_dialog_fragment)
            )
        }
    }

    private fun accountLastNameDialogFragment() {
        // when last name value is clicked
        childFragmentManager.setFragmentResultListener(
            ACCOUNT_LAST_NAME_REQUEST_KEY,
            requireActivity()
        ) { key, bundle ->
            // collect input values from dialog fragment and update the firstname text of user
            val lastName = bundle.getString(ACCOUNT_LAST_NAME_BUNDLE_KEY)
            binding.lastNameEt.text = lastName
        }

        // when last Name name value is clicked
        binding.lastNameEt.setOnClickListener {
            val currentLastName = binding.lastNameEt.text.toString()
            val bundle = bundleOf(CURRENT_ACCOUNT_LAST_NAME_BUNDLE_KEY to currentLastName)
            createProfileDialogFragment(
                R.layout.account_last_name_dialog_fragment,
                bundle
            ).show(
                childFragmentManager, MyProfile::class.java.simpleName
            )
        }
    }

    // Email Dialog
    private fun accountEmailEditDialog() {
        // when other name name value is clicked
        childFragmentManager.setFragmentResultListener(
            ACCOUNT_EMAIL_REQUEST_KEY,
            requireActivity()
        ) { key, bundle ->
            // collect input values from dialog fragment and update the otherName text of user
            val otherName = bundle.getString(ACCOUNT_EMAIL_BUNDLE_KEY)
            binding.userEmailEt.text = otherName
        }

        // when last Name name value is clicked
        binding.userEmailEt.setOnClickListener {
            val email =
                binding.userEmailEt.text.toString()
            val bundle = bundleOf(CURRENT_EMAIL_BUNDLE_KEY to email)
            createProfileDialogFragment(
                R.layout.account_email_dialog_fragment,
                bundle
            ).show(
                childFragmentManager, MyProfile::class.java.simpleName
            )
        }
    }

    private fun accountPhoneEditDialog() {
        // when other name name value is clicked
        childFragmentManager.setFragmentResultListener(
            ACCOUNT_OTHER_NAME_REQUEST_KEY,
            requireActivity()
        ) { key, bundle ->
            // collect input values from dialog fragment and update the otherName text of user
            val otherName = bundle.getString(ACCOUNT_OTHER_NAME_BUNDLE_KEY)
            binding.mobileNoEt.text = otherName
        }

        // when last Name name value is clicked
        binding.mobileNoEt.setOnClickListener {
            val currentOtherName =
                binding.mobileNoEt.text.toString()
            val bundle = bundleOf(CURRENT_ACCOUNT_OTHER_NAME_BUNDLE_KEY to currentOtherName)
            createProfileDialogFragment(
                R.layout.account_phone_number_dialog_fragment,
                bundle
            ).show(
                childFragmentManager, MyProfile::class.java.simpleName
            )
        }
    }
    companion object {
        const val ACCOUNT_FIRST_NAME_REQUEST_KEY = "ACCOUNT FIRST NAME REQUEST KEY"
        const val ACCOUNT_FIRST_NAME_BUNDLE_KEY = "ACCOUNT FIRST NAME BUNDLE KEY"
        const val CURRENT_ACCOUNT_FIRST_NAME_BUNDLE_KEY = "CURRENT ACCOUNT FIRST NAME BUNDLE KEY"

        const val ACCOUNT_LAST_NAME_REQUEST_KEY = "ACCOUNT LAST NAME REQUEST KEY"
        const val ACCOUNT_LAST_NAME_BUNDLE_KEY = "ACCOUNT LAST NAME BUNDLE KEY"
        const val CURRENT_ACCOUNT_LAST_NAME_BUNDLE_KEY = "CURRENT ACCOUNT LAST NAME BUNDLE KEY"

        const val ACCOUNT_EMAIL_REQUEST_KEY = "ACCOUNT EMAIL REQUEST KEY"
        const val ACCOUNT_EMAIL_BUNDLE_KEY = "ACCOUNT EMAIL BUNDLE KEY"
        const val CURRENT_EMAIL_BUNDLE_KEY = "CURRENT EMAIL BUNDLE KEY"

        const val ACCOUNT_OTHER_NAME_REQUEST_KEY = "ACCOUNT OTHER NAME REQUEST KEY"
        const val ACCOUNT_OTHER_NAME_BUNDLE_KEY = "ACCOUNT OTHER NAME BUNDLE KEY"
        const val CURRENT_ACCOUNT_OTHER_NAME_BUNDLE_KEY = "CURRENT ACCOUNT OTHER NAME BUNDLE KEY"

        const val READ_IMAGE_STORAGE = 102
        const val NAME = "Rez"
    }

}


