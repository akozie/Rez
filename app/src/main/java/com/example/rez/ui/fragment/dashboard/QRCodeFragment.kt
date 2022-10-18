package com.example.rez.ui.fragment.dashboard

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.api.Resource
import com.example.rez.databinding.FragmentQRCodeBinding
import com.example.rez.model.authentication.response.Booking
import com.example.rez.ui.GlideApp
import com.example.rez.ui.RezViewModel
import com.example.rez.util.handleApiError
import com.example.rez.util.showToast
import com.example.rez.util.visible
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class QRCodeFragment : Fragment() {

    private var _binding: FragmentQRCodeBinding? = null
    private val binding get() = _binding!!
    private var args: Booking? = null
    private val rezViewModel: RezViewModel by activityViewModels()
    //private lateinit var photo: String



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
        _binding = FragmentQRCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args = arguments?.getParcelable("QRCODE")
        setUpQRCode()

        binding.share.setOnClickListener {
            val b: Bitmap = getBitMap(binding.qrCode)

            try {
                val f = File(requireContext().externalCacheDir, "forShare.jpg")
                val outputStream = FileOutputStream(f)
                b.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()
                f.setReadable(true, false)


                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                val photo: Uri = FileProvider.getUriForFile(requireContext(), requireActivity().packageName + ".provider", f)
                shareIntent.putExtra(Intent.EXTRA_STREAM, photo)
                 shareIntent.type = "image/*"
                shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(Intent.createChooser(shareIntent, null))
            }catch (e:FileNotFoundException){
                e.printStackTrace()
            }catch (e:IOException){
                e.printStackTrace()
            }

        }
    }

    private fun setUpQRCode(){
        args!!.id.let {
            rezViewModel.getEachBooking(token = "Bearer ${sharedPreferences.getString("token", "token")}",
                it
            )
        }
        rezViewModel.eachBookingResponse.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)
            when(it) {
                is Resource.Success -> {
                    if (it.value.status){
                        lifecycleScope.launch {
                            Glide.with(requireContext()).load(it.value.data.qr_code).into(binding.qrCode)
                            binding.refText.text = it.value.data.booking_reference
                            //photo = it.value.data.qr_code
                        }
                    } else {
                        it.value.message.let { it1 ->
                            Toast.makeText(requireContext(), it1, Toast.LENGTH_SHORT).show() }
                    }
                }
                is Resource.Error<*> -> {
                    showToast(it.data.toString())
                    rezViewModel.eachBookingResponse.removeObservers(viewLifecycleOwner)                }
            }
        })
        rezViewModel.cleanGetBookingResponse()

    }

    //convert image to bitmap
    private fun getBitMap(view: View): Bitmap {
        val bitMap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitMap)
        val bgDrawable = view.background
        if (bgDrawable!= null){
            bgDrawable.draw(canvas)
        }else{
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return bitMap
    }

    override fun onDestroy() {
        super.onDestroy()
        rezViewModel.cleanGetBookingResponse()
        _binding = null
    }
}