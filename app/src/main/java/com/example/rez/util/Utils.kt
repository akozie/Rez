package com.example.rez.util

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.rez.R
import com.example.rez.api.Resource
import com.example.rez.ui.fragment.authentication.LoginFragment
import com.example.rez.ui.fragment.authentication.RegistrationFragment
import com.example.rez.ui.fragment.dashboard.AboutFragment
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

fun<A: Activity> Activity.startNewActivity(activity: Class<A>) {
    Intent(this, activity).also{
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
}

fun View.visible(isVisible: Boolean){
    visibility = if(isVisible) View.VISIBLE else View.GONE
}

fun View.enable(enabled: Boolean) {
    isEnabled = enabled
    alpha = if (enabled) 1f else 0.5f
}


fun View.snackbar(
    message: String,
    action: (() -> Unit)? = null
){
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
    action?.let {
        snackbar.setAction("Retry"){
            it()
        }
    }
    snackbar.show()
}


fun Fragment.handleApiError(
    failure: Resource.Failure,
    retry: (() -> Unit)? = null
) {
    when {
        failure.isNetworkError -> requireView().snackbar("Please check your internet connection", retry)
        failure.errorCode == 400 -> {
            if (this is LoginFragment) {
                requireView().snackbar("You have entered incorrect email or password")
            }
            if (this is RegistrationFragment) {
                requireView().snackbar("You have entered incorrect email or phone number")
            }
        }
//        failure.errorCode == 400 && failure.message == "No available slots for this table today" ->{
//            if (this is AboutFragment){
//                requireView().snackbar("No available slots for this table today")
//            }
//        }
//        failure.errorCode == 400 && failure.message == "Restaurant is closed at this time" ->{
//            if (this is AboutFragment){
//                requireView().snackbar("Restaurant is closed at this time")
//            }
//        }
        else -> {
            requireView().snackbar("Bad request")
        }
    }
}

    fun Fragment.uriToBitmap(uriImage: Uri): Bitmap? {

        val contentResolver = requireActivity().contentResolver
        var mBitmap: Bitmap? = null
        mBitmap = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(
                contentResolver,
                uriImage
            )
        } else {
            val source = ImageDecoder.createSource(contentResolver, uriImage)
            ImageDecoder.decodeBitmap(source)
        }
        return mBitmap
    }

fun Fragment.saveBitmap(bmp: Bitmap?): File? {
    val extStorageDirectory = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    var outStream: OutputStream? = null
    var file: File? = null
    val time = System.currentTimeMillis()
    val child = "JPEG_${time}_.jpg"

    // String temp = null;
    if (extStorageDirectory != null) {
        file = File(extStorageDirectory, child)
        if (file.exists()) {
            file.delete()
            file = File(extStorageDirectory, child)
        }
        try {
            outStream = FileOutputStream(file)
            bmp?.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
            outStream.flush()
            outStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    return file
}


fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

/**
 * An extension function for loading images from server using glide
 * with provisions for loading and error state
 */
fun ImageView.loadImage(imageUrl: String?) {
    val imgUri = imageUrl?.toUri()
    Glide.with(this)
        .load(imgUri).apply(
            RequestOptions()
                .placeholder(R.drawable.chairman_image)
                .error(R.drawable.chairman_image)
        ).into(this)
}
