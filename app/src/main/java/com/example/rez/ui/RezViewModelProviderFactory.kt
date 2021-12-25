package com.example.rez.ui

import android.app.Application
import androidx.lifecycle.*
import com.example.rez.repository.AuthRepository

class RezViewModelProviderFactory(
    val app: Application,
    val rezRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RezViewModel(app, rezRepository) as T
    }
}