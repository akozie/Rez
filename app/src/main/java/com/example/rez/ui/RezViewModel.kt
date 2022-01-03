package com.example.rez.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rez.api.Resource
import com.example.rez.model.authentication.genresponse.ForPasswordResponse
import com.example.rez.model.authentication.genresponse.LogResponse
import com.example.rez.model.authentication.request.LoginRequest
import com.example.rez.model.authentication.request.RegisterRequest
import com.example.rez.model.authentication.genresponse.RegResponse
import com.example.rez.model.authentication.genresponse.ResPasswordResponse
import com.example.rez.model.authentication.request.ForgotPasswordRequest
import com.example.rez.model.authentication.request.ResPasswordRequest
import com.example.rez.repository.AuthRepository
import kotlinx.coroutines.launch

class RezViewModel(
    app: Application,
    var rezRepository: AuthRepository
): AndroidViewModel(app) {

    private val _resetPasswordResponse: MutableLiveData<Resource<ResPasswordResponse>> = MutableLiveData()
    val resetPasswordResponse: LiveData<Resource<ResPasswordResponse>>
        get() = _resetPasswordResponse

    private val _forgotPasswordResponse: MutableLiveData<Resource<ForPasswordResponse>> = MutableLiveData()
    val forgotPasswordResponse: LiveData<Resource<ForPasswordResponse>>
        get() = _forgotPasswordResponse

    private val _registerResponse: MutableLiveData<Resource<RegResponse>> = MutableLiveData()
    val registerResponse: LiveData<Resource<RegResponse>>
        get() = _registerResponse

    private val _loginResponse: MutableLiveData<Resource<LogResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<LogResponse>>
        get() = _loginResponse



    fun resetPassword( user: ResPasswordRequest
    ) = viewModelScope.launch {
        _resetPasswordResponse.value = Resource.Loading
        _resetPasswordResponse.value = rezRepository.resetPassword(user)
    }

    fun forgotPassword( user: ForgotPasswordRequest
    ) = viewModelScope.launch {
        _forgotPasswordResponse.value = Resource.Loading
        _forgotPasswordResponse.value = rezRepository.forgotPassword(user)
    }

    fun register( user: RegisterRequest
    ) = viewModelScope.launch {
        _registerResponse.value = Resource.Loading
        _registerResponse.value = rezRepository.register(user)
    }

    fun login( user: LoginRequest
    ) = viewModelScope.launch {
        _loginResponse.value = Resource.Loading
        _loginResponse.value = rezRepository.login(user)
    }
}