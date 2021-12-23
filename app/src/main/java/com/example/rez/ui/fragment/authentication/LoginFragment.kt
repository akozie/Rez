package com.example.rez.ui.fragment.authentication

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.rez.databinding.FragmentLoginBinding
import com.example.rez.ui.activity.DashboardActivity

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginBtn: TextView

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

//        binding.loginTv.setOnClickListener {
//            val intent = Intent(requireContext(), DashboardActivity::class.java)
//            startActivity(intent)
//        }

        binding.register.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegistrationFragment()
            findNavController().navigate(action)
        }

        binding.forgotPasswordTv.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
            findNavController().navigate(action)
        }
    }
}