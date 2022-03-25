package com.example.rez.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.rez.api.RemoteDataSource.Companion.api
import com.example.rez.api.Resource
import com.example.rez.model.authentication.genresponse.*
import com.example.rez.model.authentication.request.*
import com.example.rez.model.authentication.response.*
import com.example.rez.model.dashboard.*
import com.example.rez.model.direction.DirectionResponseModel
import com.example.rez.model.paging.BookingPagingSource
import com.example.rez.model.paging.FavoritePagingSource
import com.example.rez.model.paging.ReviewPagingSource
import com.example.rez.model.paging.SearchPagingSource
import com.example.rez.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RezViewModel(
    app: Application,
    var rezRepository: AuthRepository
): AndroidViewModel(app) {

    private val _addVendorReviewResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()
    val addVendorReviewResponse: LiveData<Resource<GeneralResponse>>
        get() = _addVendorReviewResponse

    private val _deleteTableReviewResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()
    val deleteTableReviewResponse: LiveData<Resource<GeneralResponse>>
        get() = _deleteTableReviewResponse

    private val _addTableReviewResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()
    val addTableReviewResponse: LiveData<Resource<GeneralResponse>>
        get() = _addTableReviewResponse

    private val _bookTableResponse: MutableLiveData<Resource<BookTableResponse>> = MutableLiveData()
    val bookTableResponse: LiveData<Resource<BookTableResponse>>
        get() = _bookTableResponse

    private val _getProfileTableReviewResponse: MutableLiveData<Resource<ReviewResponse>> = MutableLiveData()
    val getProfileTableReviewResponse: LiveData<Resource<ReviewResponse>>
        get() = _getProfileTableReviewResponse

    private val _getProfileTableResponse: MutableLiveData<Resource<GetProfileTableResponse>> = MutableLiveData()
    val getProfileTableResponse: LiveData<Resource<GetProfileTableResponse>>
        get() = _getProfileTableResponse

    private val _getVendorTableResponse: MutableLiveData<Resource<GetVendorTableResponse>> = MutableLiveData()
    val getVendorTableResponse: LiveData<Resource<GetVendorTableResponse>>
        get() = _getVendorTableResponse

    private val _searchResponse: MutableLiveData<Resource<SearchResponse>> = MutableLiveData()
    val searchResponse: LiveData<Resource<SearchResponse>>
        get() = _searchResponse

    private val _getHomeResponse: MutableLiveData<Resource<HomeResponse>> = MutableLiveData()
    val getHomeResponse: LiveData<Resource<HomeResponse>>
        get() = _getHomeResponse

    private val _addOrRemoveFavoritesResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()
    val addOrRemoveFavoritesResponse: LiveData<Resource<GeneralResponse>>
        get() = _addOrRemoveFavoritesResponse

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


    private val _loginGoogleResponse: MutableLiveData<Resource<SocialResponse>> = MutableLiveData()
    val loginGoogleResponse: LiveData<Resource<SocialResponse>>
        get() = _loginGoogleResponse

    private val _loginWithFacebookResponse: MutableLiveData<Resource<SocialResponse>> = MutableLiveData()
    val loginWithFacebookResponse: LiveData<Resource<SocialResponse>>
        get() = _loginWithFacebookResponse

    private val _changePasswordResponse: MutableLiveData<Resource<ChangePasswordResponse>> = MutableLiveData()
    val changePasswordResponse: LiveData<Resource<ChangePasswordResponse>>
        get() = _changePasswordResponse

    private val _getDirectionResponse: MutableLiveData<Resource<DirectionResponseModel>> = MutableLiveData()
    val getDirectionResponse: LiveData<Resource<DirectionResponseModel>>
        get() = _getDirectionResponse





    fun getDirect(mode: String,
                  origin: String,
                  destination: String,
                  key: String
    ) = viewModelScope.launch {
          _getDirectionResponse.value = Resource.Loading
        _getDirectionResponse.value = rezRepository.getDirection(mode,origin, destination, key)
    }

    fun addVendorRating(token: String,
                        rating: RateVendorRequest,
                       vendorID: Int
    ) = viewModelScope.launch {
        _addVendorReviewResponse.value = Resource.Loading
        _addVendorReviewResponse.value = rezRepository.addVendorRating(token, rating, vendorID)
    }

    fun deleteTableReview(token: String,
                       reviewID: Int
    ) = viewModelScope.launch {
        _deleteTableReviewResponse.value = Resource.Loading
        _deleteTableReviewResponse.value = rezRepository.deleteTableReview(token, reviewID)
    }

    fun addTableReview(token: String,
                       tableReviewRequest: TableReviewRequest,
                       vendorID: Int,
                       tableID: Int
    ) = viewModelScope.launch {
        _addTableReviewResponse.value = Resource.Loading
        _addTableReviewResponse.value = rezRepository.addTableReview(token, tableReviewRequest, vendorID, tableID)
    }

    fun bookTable(token: String,
                 bookTableRequest: BookTableRequest
    ) = viewModelScope.launch {
        _bookTableResponse.value = Resource.Loading
        _bookTableResponse.value = rezRepository.bookTable(token, bookTableRequest)
    }


    fun getBookings(token: String) = Pager(PagingConfig(pageSize = 20, enablePlaceholders = false)){
        BookingPagingSource(api, token)
    }.flow.cachedIn(viewModelScope)

    fun getVendorProfileTableReviews(vendorID: Int, tableID: Int, token: String) = Pager(PagingConfig(pageSize = 20, enablePlaceholders = false)){
        ReviewPagingSource(vendorID, tableID, api, token)
    }.flow.cachedIn(viewModelScope)



    fun getVendorProfileTable( vendorID: Int,
                               tableID: Int,
                 token: String
    ) = viewModelScope.launch {
        _getProfileTableResponse.value = Resource.Loading
        _getProfileTableResponse.value = rezRepository.getVendorProfileTable(vendorID, tableID, token)
    }

    fun getVendorTables( vendorID: Int,
                 token: String
    ) = viewModelScope.launch {
        _getVendorTableResponse.value = Resource.Loading
        _getVendorTableResponse.value = rezRepository.getVendorTables(vendorID, token)
    }

    fun getHome( lat: Double,
                 long: Double,
                 token: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        _getHomeResponse.postValue( Resource.Loading)
        _getHomeResponse.postValue(rezRepository.getHome(lat, long, token))
    }

    fun search(search: String, noOfPersons: Int, priceFrom: Int, priceTo: Int, token: String) = Pager(PagingConfig(pageSize = 20, enablePlaceholders = false)){
        SearchPagingSource(search, noOfPersons, priceFrom,  priceTo,  api, token)
    }.flow.cachedIn(viewModelScope)


    fun getFavorites(token: String) = Pager(PagingConfig(pageSize = 20, enablePlaceholders = false)){
        FavoritePagingSource(api, token)
    }.flow.cachedIn(viewModelScope)


    fun addOrRemoveFavorites(
        id: String,
        token: String
    ) = viewModelScope.launch {
        _addOrRemoveFavoritesResponse.value = Resource.Loading
        _addOrRemoveFavoritesResponse.value = rezRepository.addOrRemoveFavorites(id, token)
    }

    fun uploadImage(image: String,
                    token: String
    ) = viewModelScope.launch {
        _uploadImageResponse.value = Resource.Loading
        _uploadImageResponse.value = rezRepository.uploadImage(image, token)
    }

    fun getProfile( token: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        _getProfileResponse.postValue(Resource.Loading)
        _getProfileResponse.postValue( rezRepository.getProfile(token))
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


    fun loginWithGoogle(token: FacebookRequest) = viewModelScope.launch {
        _loginGoogleResponse.value = Resource.Loading
        _loginGoogleResponse.value = rezRepository.loginGoogle(token)
    }

    fun loginWithFacebook(token: FacebookRequest) = viewModelScope.launch {
        _loginWithFacebookResponse.value = Resource.Loading
        _loginWithFacebookResponse.value = rezRepository.loginFacebook(token)
    }

    fun changePassword(user: ChangePasswordRequest,
                       token: String
    ) = viewModelScope.launch {
        _changePasswordResponse.value = Resource.Loading
        _changePasswordResponse.value = rezRepository.changePassword(user, token)
    }
}