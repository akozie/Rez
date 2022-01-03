package com.example.rez.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.rez.R
import com.example.rez.repository.AuthRepository
import com.example.rez.ui.RezViewModel
import com.example.rez.ui.RezViewModelProviderFactory

class MainActivity : AppCompatActivity() {

    lateinit var rezViewModel: RezViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val rezRepository = AuthRepository()
        val viewModelProviderFactory = RezViewModelProviderFactory(application, rezRepository)
        rezViewModel = ViewModelProvider(this, viewModelProviderFactory).get(RezViewModel::class.java)
    }
}