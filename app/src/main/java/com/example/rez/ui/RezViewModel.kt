package com.example.rez.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rez.api.Resource
import com.example.rez.model.authentication.genresponse.ForPasswordResponse
import com.example.rez.model.authentication.genresponse.RegResponse
import com.example.rez.model.authentication.genresponse.ResPasswordResponse
import com.example.rez.model.authentication.genresponse.UpdateProResponse
import com.example.rez.model.authentication.request.*
import com.example.rez.model.authentication.response.ChangePasswordResponse
import com.example.rez.model.authentication.response.LoginResponse
import com.example.rez.model.authentication.response.UploadImageResponse
import com.example.rez.repository.AuthRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class RezViewModel(
    app: Application,
    var rezRepository: AuthRepository
): AndroidViewModel(app) {

    private val _uploadImageResponse: MutableLiveData<Resource<UploadImageResponse>> = MutableLiveData()
    val uploadImageResponse: LiveData<Resource<UploadImageResponse>>
        get() = _uploadImageResponse

    private val _getProfileResponse: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val getProfileResponse: LiveData<Resource<LoginResponse>>
        get() = _getProfileResponse

    private val _updateProfileResponse: MutableLiveData<Resource<UpdateProResponse>> = MutableLiveData()
    val updateProfileResponse: LiveData<Resource<UpdateProResponse>>
        get() = _updateProfileResponse

    private val _resetPasswordResponse: MutableLiveData<Resource<ResPasswordResponse>> = MutableLiveData()
    val resetPasswordResponse: LiveData<Resource<ResPasswordResponse>>
        get() = _resetPasswordResponse

    private val _forgotPasswordResponse: MutableLiveData<Resource<ForPasswordResponse>> = MutableLiveData()
    val forgotPasswordResponse: LiveData<Resource<ForPasswordResponse>>
        get() = _forgotPasswordResponse

    private val _registerResponse: MutableLiveData<Resource<RegResponse>> = MutableLiveData()
    val registerResponse: LiveData<Resource<RegResponse>>
        get() = _registerResponse

    private val _loginResponse: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<LoginResponse>>
        get() = _loginResponse

    private val _changePasswordResponse: MutableLiveData<Resource<ChangePasswordResponse>> = MutableLiveData()
    val changePasswordResponse: LiveData<Resource<ChangePasswordResponse>>
        get() = _changePasswordResponse



    fun uploadImage(image: MultipartBody.Part,
                    token: String
    ) = viewModelScope.launch {
        _uploadImageResponse.value = Resource.Loading
        _uploadImageResponse.value = rezRepository.uploadImage(image, token)
    }

    fun getProfile( token: String
    ) = viewModelScope.launch {
        _getProfileResponse.value = Resource.Loading
        _getProfileResponse.value = rezRepository.getProfile(token)
    }

    fun updateProfile( user: UpdateProfileRequest,
                       token: String
    ) = viewModelScope.launch {
        _updateProfileResponse.value = Resource.Loading
        _updateProfileResponse.value = rezRepository.updateProfile(user, token)
    }

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

    fun changePassword(user: ChangePasswordRequest,
                       token: String
    ) = viewModelScope.launch {
        _changePasswordResponse.value = Resource.Loading
        _changePasswordResponse.value = rezRepository.changePassword(user, token)
    }
}