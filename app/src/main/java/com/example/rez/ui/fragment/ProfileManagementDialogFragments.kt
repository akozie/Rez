package com.example.rez.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.rez.R
import com.example.rez.databinding.*
import com.example.rez.ui.fragment.dashboard.MyProfile.Companion.ACCOUNT_EMAIL_BUNDLE_KEY
import com.example.rez.ui.fragment.dashboard.MyProfile.Companion.ACCOUNT_EMAIL_REQUEST_KEY
import com.example.rez.ui.fragment.dashboard.MyProfile.Companion.ACCOUNT_FIRST_NAME_BUNDLE_KEY
import com.example.rez.ui.fragment.dashboard.MyProfile.Companion.ACCOUNT_FIRST_NAME_REQUEST_KEY
import com.example.rez.ui.fragment.dashboard.MyProfile.Companion.ACCOUNT_LAST_NAME_BUNDLE_KEY
import com.example.rez.ui.fragment.dashboard.MyProfile.Companion.ACCOUNT_LAST_NAME_REQUEST_KEY
import com.example.rez.ui.fragment.dashboard.MyProfile.Companion.ACCOUNT_OTHER_NAME_BUNDLE_KEY
import com.example.rez.ui.fragment.dashboard.MyProfile.Companion.ACCOUNT_OTHER_NAME_REQUEST_KEY
import com.example.rez.ui.fragment.dashboard.MyProfile.Companion.CURRENT_ACCOUNT_FIRST_NAME_BUNDLE_KEY
import com.example.rez.ui.fragment.dashboard.MyProfile.Companion.CURRENT_ACCOUNT_LAST_NAME_BUNDLE_KEY
import com.example.rez.ui.fragment.dashboard.MyProfile.Companion.CURRENT_ACCOUNT_OTHER_NAME_BUNDLE_KEY
import com.example.rez.ui.fragment.dashboard.MyProfile.Companion.CURRENT_EMAIL_BUNDLE_KEY
import com.example.rez.ui.fragment.dashboard.TopFragment.Companion.ACCOUNT_FILTER_BUNDLE_KEY
import com.example.rez.ui.fragment.dashboard.TopFragment.Companion.ACCOUNT_SECOND_FILTER_BUNDLE_KEY
import com.example.rez.ui.fragment.dashboard.TopFragment.Companion.CURRENT_FILTER_NAME_BUNDLE_KEY
import com.example.rez.ui.fragment.dashboard.TopFragment.Companion.FILTER_NAME_REQUEST_KEY
import com.example.rez.ui.fragment.dashboard.TopFragment.Companion.FILTER_SECOND_NAME_REQUEST_KEY
import com.example.rez.util.showToast

class ProfileManagementDialogFragments(
    private var dialogLayoutId: Int,
    private var bundle: Bundle? = null,
    bundleSecond: Bundle?
) : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { return inflater.inflate(dialogLayoutId, container) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*Inflate Dialog Fragment Layouts based on Id*/
        when (dialogLayoutId) {
            R.layout.account_first_name_dialog_fragment -> {
                /*Initialise binding*/
                val binding = AccountFirstNameDialogFragmentBinding.bind(view)
                val firstNameEditText =
                    binding.accountFirstNameDialogFragmentFirstNameEditTextView
                val okButton = binding.accountFirstNameDialogFragmentOkButton
                val cancelButton = binding.accountFirstNameDialogFragmentCancelButton

                val retrievedArgs =
                    bundle?.getString(CURRENT_ACCOUNT_FIRST_NAME_BUNDLE_KEY)

                /*Attaching the data*/
                firstNameEditText.setText(retrievedArgs)

                /*when the dialog ok button is clicked*/
                okButton.setOnClickListener {
                    val inputValue =
                        firstNameEditText.text.toString()

                    when {
                        inputValue.isEmpty() -> {
                            binding.accountFirstNameDialogFragmentFirstNameEditTextInputLayout.error =
                                getString(
                                    R.string.required
                                )
                            binding.accountFirstNameDialogFragmentFirstNameEditTextInputLayout.errorIconDrawable =
                                null
                        }
                        else -> {
                            setFragmentResult(
                                ACCOUNT_FIRST_NAME_REQUEST_KEY,
                                bundleOf(
                                    ACCOUNT_FIRST_NAME_BUNDLE_KEY to inputValue
                                )
                            )
                            dismiss()
                        }
                    }
                }
                /*when the dialog cancel button is clicked*/
                cancelButton.setOnClickListener {
                    dismiss()
                }

                /*Validate Dialog Fields onTextChange*/
                firstNameEditText.doOnTextChanged { _, _, _, _ ->
                    when {
                        firstNameEditText.text!!.trim().isEmpty() -> {
                            binding.accountFirstNameDialogFragmentFirstNameEditTextInputLayout.error =
                                getString(
                                    R.string.required
                                )
                            binding.accountFirstNameDialogFragmentFirstNameEditTextInputLayout.errorIconDrawable =
                                null
                        }
                        else -> {
                            binding.accountFirstNameDialogFragmentFirstNameEditTextInputLayout.error =
                                null
                        }
                    }
                }
            }
            R.layout.account_last_name_dialog_fragment -> {
                /*Initialise binding*/
                val binding = AccountLastNameDialogFragmentBinding.bind(view)
                val lastNameEditText =
                    binding.accountLastNameDialogFragmentLastNameEditTextView
                val okButton = binding.accountLastNameDialogFragmentOkButton
                val cancelButton = binding.accountLastNameDialogFragmentCancelButton

                val retrievedArgs =
                    bundle?.getString(CURRENT_ACCOUNT_LAST_NAME_BUNDLE_KEY)

                /*Attaching the data*/
                lastNameEditText.setText(retrievedArgs)

                /*when the dialog ok button is clicked*/
                okButton.setOnClickListener {
                    val inputValue =
                        lastNameEditText.text.toString()

                    when {
                        inputValue.isEmpty() -> {
                            binding.accountLastNameDialogFragmentLastNameEditTextInputLayout.error =
                                getString(
                                    R.string.required
                                )
                            binding.accountLastNameDialogFragmentLastNameEditTextInputLayout.errorIconDrawable =
                                null
                        }
                        else -> {
                            setFragmentResult(
                                ACCOUNT_LAST_NAME_REQUEST_KEY,
                                bundleOf(
                                    ACCOUNT_LAST_NAME_BUNDLE_KEY to inputValue
                                )
                            )
                            dismiss()
                        }
                    }
                }
                /*when the dialog cancel button is clicked*/
                cancelButton.setOnClickListener {
                    dismiss()
                }

                /*Validate Dialog Fields onTextChange*/
                lastNameEditText.doOnTextChanged { _, _, _, _ ->
                    when {
                        lastNameEditText.text!!.trim().isEmpty() -> {
                            binding.accountLastNameDialogFragmentLastNameEditTextInputLayout.error =
                                getString(
                                    R.string.required
                                )
                            binding.accountLastNameDialogFragmentLastNameEditTextInputLayout.errorIconDrawable =
                                null
                        }
                        else -> {
                            binding.accountLastNameDialogFragmentLastNameEditTextInputLayout.error =
                                null
                        }
                    }
                }
            }
            R.layout.account_phone_number_dialog_fragment -> {
                /*Initialise binding*/
                val binding = AccountPhoneNumberDialogFragmentBinding.bind(view)
                val phoneNumberEditText =
                    binding.accountPhoneNumberDialogFragmentOtherNameEditTextView
                val okButton = binding.accountPhoneNumberDialogFragmentOkButton
                val cancelButton = binding.accountPhoneNumberDialogFragmentCancelButton

                val retrievedArgs =
                    bundle?.getString(CURRENT_ACCOUNT_OTHER_NAME_BUNDLE_KEY)

                /*Attaching the data*/
                phoneNumberEditText.setText(retrievedArgs)

                /*when the dialog ok button is clicked*/
                okButton.setOnClickListener {
                    val inputValue =
                        phoneNumberEditText.text.toString()

                    when {
                        inputValue.isNullOrEmpty() -> {
                            setFragmentResult(
                                ACCOUNT_OTHER_NAME_REQUEST_KEY,
                                bundleOf(
                                    ACCOUNT_OTHER_NAME_BUNDLE_KEY to null
                                )
                            )
                            dismiss()
                        }
                        else -> {
                            setFragmentResult(
                                ACCOUNT_OTHER_NAME_REQUEST_KEY,
                                bundleOf(
                                    ACCOUNT_OTHER_NAME_BUNDLE_KEY to inputValue
                                )
                            )
                            dismiss()
                        }
                    }
                }
                /*when the dialog cancel button is clicked*/
                cancelButton.setOnClickListener {
                    dismiss()
                }
                /*Validate Dialog Fields onTextChange*/
                phoneNumberEditText.doOnTextChanged { _, _, _, _ ->
                    when {
                        phoneNumberEditText.text!!.trim().isEmpty() -> {
                            binding.accountPhoneNumberDialogFragmentOtherNameEditTextInputLayout.error =
                                getString(
                                    R.string.required
                                )
                            binding.accountPhoneNumberDialogFragmentOtherNameEditTextInputLayout.errorIconDrawable =
                                null
                        }
                        else -> {
                            binding.accountPhoneNumberDialogFragmentOtherNameEditTextInputLayout.error =
                                null
                        }
                    }
                }
            }

            R.layout.account_email_dialog_fragment -> {
                /*Initialise binding*/
                val binding = AccountEmailDialogFragmentBinding.bind(view)
                val phoneNumberEditText =
                    binding.accountPhoneNumberDialogFragmentOtherNameEditTextView
                val okButton = binding.accountPhoneNumberDialogFragmentOkButton
                val cancelButton = binding.accountPhoneNumberDialogFragmentCancelButton

                val retrievedArgs =
                    bundle?.getString(CURRENT_EMAIL_BUNDLE_KEY)

                /*Attaching the data*/
                phoneNumberEditText.setText(retrievedArgs)

                /*when the dialog ok button is clicked*/
                okButton.setOnClickListener {
                    val inputValue =
                        phoneNumberEditText.text.toString()

                    when {
                        inputValue.isEmpty() -> {
                            setFragmentResult(
                                ACCOUNT_EMAIL_REQUEST_KEY,
                                bundleOf(
                                    ACCOUNT_EMAIL_BUNDLE_KEY to null
                                )
                            )
                            dismiss()
                        }
                        else -> {
                            setFragmentResult(
                                ACCOUNT_EMAIL_REQUEST_KEY,
                                bundleOf(
                                    ACCOUNT_EMAIL_BUNDLE_KEY to inputValue
                                )
                            )
                            dismiss()
                        }
                    }
                }
                /*when the dialog cancel button is clicked*/
                cancelButton.setOnClickListener {
                    dismiss()
                }
                /*Validate Dialog Fields onTextChange*/
                phoneNumberEditText.doOnTextChanged { _, _, _, _ ->
                    when {
                        phoneNumberEditText.text!!.trim().isEmpty() -> {
                            binding.accountPhoneNumberDialogFragmentOtherNameEditTextInputLayout.error =
                                getString(
                                    R.string.required
                                )
                            binding.accountPhoneNumberDialogFragmentOtherNameEditTextInputLayout.errorIconDrawable =
                                null
                        }
                        else -> {
                            binding.accountPhoneNumberDialogFragmentOtherNameEditTextInputLayout.error =
                                null
                        }
                    }
                }
            }

            R.layout.account_filter_dialog_fragment -> {
                /*Initialise binding*/
                val binding = AccountFilterDialogFragmentBinding.bind(view)
                val showText =
                    binding.showText
                val showPrice = binding.showTextPrice
                val okButton = binding.applyTv
                val cancelButton = binding.cancelTv
                val removeBtn = binding.cancelDialog

                val retrievedArgs =
                    bundle?.getString(CURRENT_FILTER_NAME_BUNDLE_KEY)

                /*Attaching the data*/
               // showText.setText(retrievedArgs)

                binding.guestSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                        showText.text = p1.toString()
                    }

                    override fun onStartTrackingTouch(p0: SeekBar?) {

                    }

                    override fun onStopTrackingTouch(p0: SeekBar?) {

                    }

                })

                binding.increase.setOnClickListener {
                    showPrice.text = (showPrice.text.toString().toInt()+1).toString()
                }
                binding.decrease.setOnClickListener {
                    showPrice.text = (showPrice.text.toString().toInt()-1).toString()
                }


                binding.priceSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                        showPrice.text = p1.toString()
                    }

                    override fun onStartTrackingTouch(p0: SeekBar?) {

                    }

                    override fun onStopTrackingTouch(p0: SeekBar?) {

                    }
                })



                /*when the dialog ok button is clicked*/
                okButton.setOnClickListener {
                    val inputValue =
                        showText.text.toString()

                    val inputSecondValue =
                        showPrice.text.toString()

                    when {
                        inputValue.isEmpty() -> {
                            setFragmentResult(
                                FILTER_NAME_REQUEST_KEY,
                                bundleOf(
                                    ACCOUNT_FILTER_BUNDLE_KEY to null
                                )
                            )
                            dismiss()
                        }
                        inputSecondValue.isEmpty() -> {
                            setFragmentResult(
                                FILTER_SECOND_NAME_REQUEST_KEY,
                                bundleOf(
                                    ACCOUNT_SECOND_FILTER_BUNDLE_KEY to null
                                )
                            )
                            dismiss()
                        }
                        else -> {
                            setFragmentResult(
                                FILTER_NAME_REQUEST_KEY,
                                bundleOf(
                                    ACCOUNT_FILTER_BUNDLE_KEY to inputValue
                                )
                            )
                            setFragmentResult(
                                FILTER_SECOND_NAME_REQUEST_KEY,
                                bundleOf(
                                    ACCOUNT_SECOND_FILTER_BUNDLE_KEY to inputSecondValue
                                )
                            )
                            dismiss()
                        }
                    }
                }
                /*when the dialog cancel button is clicked*/
                cancelButton.setOnClickListener {
                    dismiss()
                }
                removeBtn.setOnClickListener {
                    dismiss()
                }
            }

        }
    }

    companion object {
        fun createProfileDialogFragment(
            layoutId: Int,
            bundle: Bundle? = null,
            bundleSecond: Bundle? = null
        ): ProfileManagementDialogFragments {
            return ProfileManagementDialogFragments(layoutId, bundle, bundleSecond)
        }
    }
}
