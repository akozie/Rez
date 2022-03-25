package com.example.rez.ui.fragment.dashboard

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.rez.databinding.FragmentSuccessBinding

class SuccessFragment : DialogFragment() {


    private var _binding: FragmentSuccessBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val window = dialog!!.window;
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.CENTER)

            dialog!!.show()

            binding.okTv.setOnClickListener {
                val action = SuccessFragmentDirections.actionSuccessFragmentToHome2()
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}