package com.example.rez.ui.fragment.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rez.R
import com.example.rez.databinding.FragmentQRCodeBinding

class QRCodeFragment : Fragment() {

    private var _binding: FragmentQRCodeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentQRCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

}