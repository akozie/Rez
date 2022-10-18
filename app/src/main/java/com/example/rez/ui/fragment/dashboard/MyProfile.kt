package com.example.rez.ui.fragment.dashboard

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.api.NameInterface
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentMyProfileBinding
import com.example.rez.model.authentication.request.UpdateProfileRequest
import com.example.rez.ui.GlideApp
import com.example.rez.ui.RezViewModel
import com.example.rez.ui.activity.DashboardActivity
import com.example.rez.ui.fragment.ProfileManagementDialogFragments.Companion.createProfileDialogFragment
import com.example.rez.util.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImage.getPickImageChooserIntent
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.ByteArrayOutputStream
import javax.inject.Inject


class MyProfile : Fragment() {

    private var _binding: FragmentMyProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var rezViewModel: RezViewModel
    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>
    lateinit var dataPasser: NameInterface



    @Inject
    lateinit var sharedPreferences: SharedPreferences

    /*function to crop picture*/
    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setCropMenuCropButtonTitle("Done")
                .setAspectRatio(1, 1)
                .getIntent(this@MyProfile.context!!)
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
        rezViewModel = (activity as DashboardActivity).rezViewModel
          binding.saveBtn.progressBar.visibility = View.VISIBLE

        //GET USER PROFILE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.saveBtn.button.text = "Update Profile"
        /*Dialog fragment functions*/
        accountFirstNameEditDialog()
        accountLastNameDialogFragment()
        //accountEmailEditDialog()
        accountPhoneEditDialog()

        getProfile()

        /*Initialize Image Cropper*/
        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) {
            it?.let { uri ->
                binding.customerImageIv.imageAlpha = 140
                uploadImageToServer(uri)
            }
        }
        /*Select profile image*/
        binding.imageSelectIv.setOnClickListener {
//            startActivityForResult(getPickImageChooserIntent(requireContext()), 200)
            Manifest.permission.READ_EXTERNAL_STORAGE.checkForPermission(NAME, READ_IMAGE_STORAGE)
        }

        binding.saveBtn.progressBar.visible(false)

        binding.saveBtn.submit.setOnClickListener {
            if (binding.firstNameEt.text!!.isEmpty()) {
                val message = "All inputs are required"
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            } else {
                binding.saveBtn.submit.enable(true)
                updateProfile()
            }
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataPasser = context as NameInterface
    }
    fun passData(data: String){
        dataPasser.onDataPass(data)
    }
    private fun updateProfile() {
        val firstName = binding.firstNameEt.text.toString().trim()
        val lastName = binding.lastNameEt.text.toString().trim()
        val email = binding.userEmailEt.text.toString().trim()
        val phoneNumber = binding.mobileNoEt.text.toString().trim()

        when {
            email.isEmpty() -> {
                Toast.makeText(requireContext(), R.string.all_email_cant_be_empty, Toast.LENGTH_SHORT).show()

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
                rezViewModel.updateProfile(newUser, token = "Bearer ${sharedPreferences.getString("token", "token")}")
                rezViewModel.updateProfileResponse.observe(viewLifecycleOwner, Observer {
                    binding.saveBtn.progressBar.visible(it is Resource.Loading)
                    binding.saveBtn.button.text =  "Please wait.."
                    when(it) {
                        is Resource.Success -> {
                            binding.saveBtn.button.text = "Update Profile"
                            if (it.value.status){
                                    val message = it.value.message
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                                val newName = binding.firstNameEt.text.toString()
                                sharedPreferences.edit().putString("name", newName).apply()
                                passData(newName)
                                    val action = MyProfileDirections.actionMyProfileToSuccessFragment()
                                    findNavController().navigate(action)
                            } else {
                                it.value.message.let { it1 ->
                                    Toast.makeText(requireContext(), it1, Toast.LENGTH_SHORT).show() }
                            }
                            //rezViewModel.cleanProfileResponse()
                        }
                        is Resource.Error<*> -> {
                            binding.saveBtn.button.text = "Update Profile"
                            showToast(it.data.toString())
                            rezViewModel.updateProfileResponse.removeObservers(viewLifecycleOwner)
                        }
                    }
                })
            }
        }
    }

    //get profile
    private fun getProfile() {
        rezViewModel.getProfile(token = "Bearer ${sharedPreferences.getString("token", "token")}")
        rezViewModel.getProfileResponse.observe(viewLifecycleOwner, Observer {
            binding.saveBtn.progressBar.visible(it is Resource.Loading)
            binding.saveBtn.button.text = "Please wait..."
                when(it) {
                is Resource.Success -> {
                    binding.saveBtn.button.text = "Update Profile"
                    if (it.value.status){
                        lifecycleScope.launch {
                            binding.firstNameEt.text = it.value.data.first_name
                            binding.lastNameEt.text = it.value.data.last_name
                            binding.userEmailEt.text = sharedPreferences.getString("email", "email")
                            //binding.mobileNoEt.text = it.value.data.phone?.substring(4)
                            if (it.value.data.phone.isNullOrEmpty()){
                                binding.mobileNoEt.text = "+234"
                            }else{
                                binding.mobileNoEt.text = it.value.data.phone
                            }
                            if (it.value.data.avatar == null){
                                GlideApp.with(requireContext()).load(R.drawable.chairman_image).into(binding.customerImageIv)
                            }else {
                                GlideApp.with(requireContext()).load(it.value.data.avatar).into(binding.customerImageIv)
                            }
                        }
                    } else {
                        it.value.message.let { it1 ->
                            Toast.makeText(requireContext(), it1, Toast.LENGTH_SHORT).show() }
                    }
                }
                is Resource.Error<*> -> {
                    binding.saveBtn.button.text = "Update Profile"
                    showToast(it.data.toString())
                    rezViewModel.getProfileResponse.removeObservers(viewLifecycleOwner)                }
            }
        })

    }

    /*Check for Gallery Permission*/
    private fun String.checkForPermission(name: String, requestCode: Int) {

        val cameraPermission =
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    this
                ) == PackageManager.PERMISSION_GRANTED || cameraPermission == PackageManager.PERMISSION_DENIED -> {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.CAMERA),
                        requestCode
                    )
                    cropActivityResultLauncher.launch(null)
                }
                shouldShowRequestPermissionRationale(this) -> showDialog(this, name, requestCode);

                else -> ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(this),
                    requestCode
                )
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



    /*Upload Profile Picture*/
    private fun uploadImageToServer(uri: Uri) {
        val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val bbb = outputStream.toByteArray()
        val b = Base64.encodeToString(bbb, Base64.DEFAULT).replace("\n", "")

        rezViewModel.uploadImage(b, token = "Bearer ${sharedPreferences.getString("token", "token")}")

        /*Handling the response from the retrofit*/
        rezViewModel.uploadImageResponse.observe(
            viewLifecycleOwner, Observer {
                binding.saveBtn.progressBar.visible(it is Resource.Loading)
                binding.saveBtn.button.text = "Please wait..."
                    when(it) {
                    is Resource.Success -> {
                        binding.saveBtn.button.text = "Update Profile"
                        if (it.value.status){
                            lifecycleScope.launch {
                                val message = it.value.message
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                                GlideApp.with(requireContext()).load(uri).into(binding.customerImageIv)
                            }
                        } else {
                            it.value.message.let { it1 ->
                                Toast.makeText(requireContext(), it1, Toast.LENGTH_SHORT).show() }
                        }
                        //rezViewModel.cleanImageProfile()
                    }
                    is Resource.Error<*> -> {
                        binding.saveBtn.button.text = "Update Profile"
                        showToast(it.data.toString())
                        rezViewModel.uploadImageResponse.removeObservers(viewLifecycleOwner)
                        //rezViewModel.cleanImageProfile()
                    }
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

    private fun accountPhoneEditDialog() {
        // when phone number value is clicked
        childFragmentManager.setFragmentResultListener(
            ACCOUNT_OTHER_NAME_REQUEST_KEY,
            requireActivity()
        ) { key, bundle ->
            // collect input values from dialog fragment and update the phone number of user
            val otherName = bundle.getString(ACCOUNT_OTHER_NAME_BUNDLE_KEY)
            binding.mobileNoEt.text = otherName
        }

        // when phone number value is clicked
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



    override fun onDestroy() {
        super.onDestroy()
        rezViewModel.cleanImageProfile()
        rezViewModel.cleanProfileResponse()
        _binding = null
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


