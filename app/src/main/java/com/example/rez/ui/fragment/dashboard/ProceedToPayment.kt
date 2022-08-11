package com.example.rez.ui.fragment.dashboard

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import co.paystack.android.Paystack
import co.paystack.android.PaystackSdk
import co.paystack.android.Transaction
import co.paystack.android.model.Card
import co.paystack.android.model.Charge
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.model.authentication.request.LoginRequest
import com.example.rez.model.dashboard.Table
import com.example.rez.ui.RezViewModel
import com.example.rez.util.ValidationObject
import com.example.rez.util.enable
import com.example.rez.util.showToast
import com.google.android.material.textfield.TextInputLayout
import javax.inject.Inject

import android.text.TextUtils
import androidx.annotation.RequiresApi
import com.example.rez.databinding.FragmentProceedToPaymentBinding
import java.text.*
import java.util.*
import java.util.stream.IntStream


class ProceedToPayment : Fragment() {

    private var _binding: FragmentProceedToPaymentBinding? = null
    private val binding get() = _binding!!
    private lateinit var argsDate: String
    private lateinit var argsTime: String
    private lateinit var tableCapacity:  String
    private lateinit var grandTotal:  String
    private lateinit var tableName:  String
    private lateinit var mCardNumber: EditText
    private lateinit var mCardExpiry: EditText
    private lateinit var mCardCVV: EditText
    private  val space:Char = ' '

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
        _binding = FragmentProceedToPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.paymentTv.button.text =  "Proceed To Payment"
        initializePaystack()
        initializeFormVariables()

        tableName = sharedPreferences.getString("tablename", "tablename").toString()
        tableCapacity = sharedPreferences.getString("tablequantity", "tablequantity").toString()
        grandTotal = sharedPreferences.getString("amount", "amount").toString()


        argsDate = arguments?.getString("INTVAL")!!
        argsTime = arguments?.getString("INTVALUE")!!
        binding.dateTv.text = argsDate
        if (argsTime.substring(0,2) < 12.toString()){
            binding.timeTv.text = argsTime + " AM"
        } else {
            val newTime = argsTime.substring(0,2).toInt()
            val calculatedTime = newTime - 12
            val remainingTime = argsTime.substring(2)
            if (calculatedTime == 0){
                binding.timeTv.text = "12$remainingTime PM"
            }else if (calculatedTime < 10){
                binding.timeTv.text = "0$calculatedTime$remainingTime PM"
            } else{
                binding.timeTv.text = "$calculatedTime$remainingTime PM"
            }
        }
        binding.peopleQTYTv.text = tableCapacity
        binding.tableNameTv.text = tableName
        binding.priceTv.text = grandTotal


        binding.paymentTv.submit.setOnClickListener {
            if(binding.cardNumberEt.text.toString().isEmpty() ||binding.cvvNumberEt.text.toString().isEmpty() || binding.expiryDateEt.text.toString().isEmpty() ){
                showToast("Enter card details")
            } else{
                binding.paymentTv.submit.enable(false)
                //   binding.progressBar.visibility = View.VISIBLE
                performCharge()
            }
        }

    }

    private fun initializePaystack() {
        PaystackSdk.initialize(activity?.applicationContext)
        PaystackSdk.setPublicKey(getString(R.string.PSTK_PUBLIC_KEY))
    }

    private fun initializeFormVariables() {
        mCardNumber = binding.cardNumberEt
        mCardExpiry = binding.expiryDateEt
        mCardCVV = binding.cvvNumberEt
        // this is used to add space between the cards number.
        // After the number is entered, space is added
        mCardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?,
                                           start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            @RequiresApi(Build.VERSION_CODES.N)
            override fun afterTextChanged(s: Editable?) {
                // Remove spacing char
                // Remove spacing char
                if (s!!.isNotEmpty() && s.length % 5 == 0 && s.length < 12) {
                    val c: Char = s[s.length - 1]
                    if (space == c) {
                        s.delete(s.length - 1, s.length)
                    }
                }
                // Insert char where needed.
                // Insert char where needed.
                if (s.isNotEmpty() && s.length % 5 == 0) {
                    val c: Char = s[s.length - 1]
                    // Only if its a digit where there should be a space we insert a space
                    if (Character.isDigit(c) && TextUtils.split(
                            s.toString(),
                            java.lang.String.valueOf(space)
                        ).size <= 3
                    ) {
                        s.insert(s.length - 1, java.lang.String.valueOf(space))
                    }
                }
            }
        })
        // this is used to add a forward slash (/) between the cards expiry month
        // and year (11/21). After the month is entered, a forward slash is added
        // before the year
        mCardExpiry.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?,
                                           start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length == 2 && !s.toString().contains("/")) {
                    s!!.append("/")
                }
            }
        })

    }

    private fun performCharge() {
        val cardNumber = mCardNumber.text.toString().trim()
        val cardExpiry = mCardExpiry.text.toString().trim()
        val cvv = mCardCVV.text.toString().trim()
        when {
            cardNumber.isEmpty() -> {
                showToast("Enter your card number")
            }
            cardExpiry.isEmpty() -> {
                showToast("Enter your card expiry date")
            }
            cvv.isEmpty() -> {
                showToast("Enter your cvv")
            }
            else -> {
                val cardExpiryArray = cardExpiry.split("/").toTypedArray()
                val expiryMonth = cardExpiryArray[0].toInt()
                val expiryYear = cardExpiryArray[1].toInt()
                val card = Card(cardNumber, expiryMonth, expiryYear, cvv)
                val charge = Charge()

                charge.amount = grandTotal.toInt() * 100
                charge.email = sharedPreferences.getString("email", "email")
                charge.card = card
                charge.reference =sharedPreferences.getString("ref", "ref")
                PaystackSdk.chargeCard(requireActivity(), charge, object : Paystack.TransactionCallback {
                    override fun onSuccess(transaction: Transaction) {
                        binding.paymentTv.button.text =  "Proceed To Payment"
                        binding.paymentTv.progressBar.enable(false)
                        val action = ProceedToPaymentDirections.actionProceedToPaymentToSuccessFragment()
                        findNavController().navigate(action)
                    }
                    override fun beforeValidate(transaction: Transaction) {
                        binding.paymentTv.progressBar.enable(true)
                        Log.d("Main Activity", "beforeValidate: " + transaction.reference)
                    }
                    override fun onError(error: Throwable, transaction: Transaction) {
                        binding.paymentTv.button.text =  "Proceed To Payment"
                        Log.d("Main Activity", "onError: " + error.localizedMessage)
                        val action = ProceedToPaymentDirections.actionProceedToPaymentToErrorFragment()
                        findNavController().navigate(action)
                    }
                })
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}