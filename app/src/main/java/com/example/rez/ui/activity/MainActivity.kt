package com.example.rez.ui.activity 

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.repository.AuthRepository
import com.example.rez.ui.RezViewModel
import com.example.rez.ui.RezViewModelProviderFactory
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    lateinit var rezViewModel: RezViewModel

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ( application as RezApp).localComponent?.inject(this)

        if (!sharedPreferences.all.containsKey("token")){
            setContentView(R.layout.activity_main)
        } else if (sharedPreferences.all.containsKey("token")){
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        val rezRepository = AuthRepository()
        val viewModelProviderFactory = RezViewModelProviderFactory(application, rezRepository)
        rezViewModel = ViewModelProvider(this, viewModelProviderFactory).get(RezViewModel::class.java)
    }
}