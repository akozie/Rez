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
import com.example.rez.model.paging.*
import com.example.rez.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RezViewModel(
    app: Application,
    var rezRepository: AuthRepository
): AndroidViewModel(app) {

    private val _addVendorReviewResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()
    val addVendorReviewResponse: LiveData<Resource<GeneralResponse>>
        get() = _addVendorReviewResponse

//    private val _getVendorStateResponse: MutableLiveData<Resource<StateResponse>> = MutableLiveData()
//    val getVendorStateResponse: LiveData<Resource<StateResponse>>
//        get() = _getVendorStateResponse

    private val _getTablesResponse: MutableLiveData<Resource<GetTablesResponse>> = MutableLiveData()
    val getTablesResponse: LiveData<Resource<GetTablesResponse>>
        get() = _getTablesResponse

    private val _getOpeningHoursResponse: MutableLiveData<Resource<GetOpeningHoursResponse>> = MutableLiveData()
    val getOpeningHoursResponse: LiveData<Resource<GetOpeningHoursResponse>>
        get() = _getOpeningHoursResponse

    private val _getVendorCategoryResponse: MutableLiveData<Resource<GetVendorCategoryResponse>> = MutableLiveData()
    val getVendorCategoryResponse: LiveData<Resource<GetVendorCategoryResponse>>
        get() = _getVendorCategoryResponse

    private val _deleteTableReviewResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()
    val deleteTableReviewResponse: LiveData<Resource<GeneralResponse>>
        get() = _deleteTableReviewResponse

    private val _addTableReviewResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()
    val addTableReviewResponse: LiveData<Resource<GeneralResponse>>
        get() = _addTableReviewResponse

    private val _eachBookingResponse: MutableLiveData<Resource<GetEachBookingResponse>> = MutableLiveData()
    val eachBookingResponse: LiveData<Resource<GetEachBookingResponse>>
        get() = _eachBookingResponse

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

    private val _getFavoritesCover: MutableLiveData<Resource<FavoritesCoverResponse>> = MutableLiveData()
    val getFavoritesCover: LiveData<Resource<FavoritesCoverResponse>>
        get() = _getFavoritesCover

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

//    private val _registerResponse: MutableLiveData<Resource<RegResponse>> = MutableLiveData()
//    val registerResponse: LiveData<Resource<RegResponse>>
//        get() = _registerResponse

    private val _regResponse: MutableLiveData<Resource<RegResponse>> = MutableLiveData()
    val regResponse: LiveData<Resource<RegResponse>>
        get() = _regResponse

//    private val _registerResp: MutableLiveData<Resource<RegResponse>> = MutableLiveData()
//    val registerResp: LiveData<Resource<RegResponse>>
//        get() = _registerResp

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

    private val _complaintResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()
    val complaintResponse: LiveData<Resource<GeneralResponse>>
        get() = _complaintResponse

    private val _getDirectionResponse: MutableLiveData<Resource<DirectionResponseModel>> = MutableLiveData()
    val getDirectionResponse: LiveData<Resource<DirectionResponseModel>>
        get() = _getDirectionResponse


    private val _getPlacesResponse: MutableLiveData<Resource<MainPojo>> = MutableLiveData()
    val getPlacesResponse: LiveData<Resource<MainPojo>>
        get() = _getPlacesResponse


    private val _logoutResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()
    val logoutResponse: LiveData<Resource<GeneralResponse>>
        get() = _logoutResponse


    private val _fcmTokenResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()
    val fcmTokenResponse: LiveData<Resource<GeneralResponse>>
        get() = _fcmTokenResponse

    private val _pendingReviewsResponse: MutableLiveData<Resource<PendingReviewResponse>> = MutableLiveData()
    val pendingReviewsResponse: LiveData<Resource<PendingReviewResponse>>
        get() = _pendingReviewsResponse


    private val _notificationCountResponse: MutableLiveData<Resource<NotificationCountResonse>> = MutableLiveData()
    val notificationCountResponse: LiveData<Resource<NotificationCountResonse>>
        get() = _notificationCountResponse


    private val _phoneNumber: MutableLiveData<String> = MutableLiveData()
    val phoneNumber: LiveData<String>
        get() = _phoneNumber



    fun updatePhoneNumber(phoneNumber:String){
        _phoneNumber.value = phoneNumber
    }


    fun getPlace(input: String,
                 key: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        _getPlacesResponse.postValue(Resource.Loading)
        _getPlacesResponse.postValue(rezRepository.getPlace(input, key))
    }

    fun getDirect(mode: String,
                  origin: String,
                  destination: String,
                  key: String
    ) = viewModelScope.launch {
          _getDirectionResponse.postValue(Resource.Loading)
        _getDirectionResponse.postValue(rezRepository.getDirection(mode,origin, destination, key))
    }

//    fun addVendorRating(token: String,
//                        rating: RateVendorRequest,
//                       vendorID: String
//    ) = viewModelScope.launch(Dispatchers.IO) {
//        _addVendorReviewResponse.postValue( Resource.Loading)
//        _addVendorReviewResponse.postValue( rezRepository.addVendorRating(token, rating, vendorID))
//    }

//    fun getVendorStates(
//    ) = viewModelScope.launch(Dispatchers.IO) {
//        _getVendorStateResponse.postValue( Resource.Loading)
//        _getVendorStateResponse.postValue( rezRepository.getVendorStates())
//    }

    fun pendingReviews( token: String,
    ) = viewModelScope.launch(Dispatchers.IO) {
        _pendingReviewsResponse.postValue( Resource.Loading)
        _pendingReviewsResponse.postValue( rezRepository.pendingReviews(token))
    }

    fun countNotification( token: String,
    ) = viewModelScope.launch(Dispatchers.IO) {
        _notificationCountResponse.postValue( Resource.Loading)
        _notificationCountResponse.postValue( rezRepository.countNotification(token))
    }

    fun fcmToken( token: String,
                  fcmTokenRequest: FcmTokenRequest
    ) = viewModelScope.launch(Dispatchers.IO) {
        _fcmTokenResponse.postValue( Resource.Loading)
        _fcmTokenResponse.postValue( rezRepository.fcmToken(token, fcmTokenRequest))
    }

    fun logout( token: String,
    ) = viewModelScope.launch {
        _logoutResponse.postValue( Resource.Loading)
        _logoutResponse.postValue( rezRepository.logout(token))
    }

    fun getTable( token: String,
                         vendorProfileID:String
    ) = viewModelScope.launch(Dispatchers.IO) {
        _getTablesResponse.postValue( Resource.Loading)
        _getTablesResponse.postValue( rezRepository.getTable(token, vendorProfileID))
    }

    fun getOpeningHours( token: String,
                         vendorProfileID:String
    ) = viewModelScope.launch(Dispatchers.IO) {
        _getOpeningHoursResponse.postValue( Resource.Loading)
        _getOpeningHoursResponse.postValue( rezRepository.getOpeningHours(token, vendorProfileID))
    }

    fun getVendorCategories(
    ) = viewModelScope.launch(Dispatchers.IO) {
        _getVendorCategoryResponse.postValue( Resource.Loading)
        _getVendorCategoryResponse.postValue( rezRepository.getVendorCategories())
    }

    fun deleteTableReview(token: String,
                       reviewID: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        _deleteTableReviewResponse.postValue( Resource.Loading)
        _deleteTableReviewResponse.postValue( rezRepository.deleteTableReview(token, reviewID))
    }

    fun addTableReview(token: String,
                       tableReviewRequest: TableReviewRequest,
                       id: String,
    ) = viewModelScope.launch(Dispatchers.IO) {
        _addTableReviewResponse.postValue( Resource.Loading)
        _addTableReviewResponse.postValue( rezRepository.addTableReview(token, tableReviewRequest, id))
    }

    fun bookTable(token: String,
                 bookTableRequest: BookTableRequest
    ) = viewModelScope.launch(Dispatchers.IO) {
        _bookTableResponse.postValue( Resource.Loading)
        _bookTableResponse.postValue( rezRepository.bookTable(token, bookTableRequest))
    }


    fun getEachBooking(token: String,
                 bookingID: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        _eachBookingResponse.postValue( Resource.Loading)
        _eachBookingResponse.postValue( rezRepository.getEachBooking(token, bookingID))
    }

    fun getBookings(token: String) = Pager(PagingConfig(pageSize = 20, enablePlaceholders = false)){
        BookingPagingSource(api, token)
    }.flow.cachedIn(viewModelScope)

    fun getNotification(token: String) = Pager(PagingConfig(pageSize = 20, enablePlaceholders = false)){
        NotificationPagingSource(api, token)
    }.flow.cachedIn(viewModelScope)

    fun getVendorProfileTableReviews(vendorID: String, tableID: String, token: String) = Pager(PagingConfig(pageSize = 20, enablePlaceholders = false)){
        ReviewPagingSource(vendorID, tableID, api, token)
    }.flow.cachedIn(viewModelScope)



    fun getVendorProfileTable( vendorID: String,
                               tableID: String,
                 token: String
    ) = viewModelScope.launch {
        _getProfileTableResponse.value = Resource.Loading
        _getProfileTableResponse.value = rezRepository.getVendorProfileTable(vendorID, tableID, token)
    }

    fun getVendorTables( vendorID: String,
                 token: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        _getVendorTableResponse.postValue( Resource.Loading)
        _getVendorTableResponse.postValue( rezRepository.getVendorTables(vendorID, token))
    }

    fun getHome( lat: Double,
                 long: Double,
                 token: String
    ) = viewModelScope.launch(Dispatchers.IO) {
            _getHomeResponse.postValue( Resource.Loading)
            _getHomeResponse.postValue( rezRepository.getHome(lat, long, token))
    }

    fun search(lat: Double?, lng: Double?, noOfPersons: String?, priceFrom: Int?, priceTo: Int?, stateId: Int?, type: Int?, token: String) = Pager(PagingConfig(pageSize = 20, enablePlaceholders = false)){
        SearchPagingSource(lat, lng, noOfPersons, priceFrom,  priceTo, stateId, type, api, token)
    }.flow.cachedIn(viewModelScope)


    fun getFavoritesState(token: String, stateId: Int) = Pager(PagingConfig(pageSize = 20, enablePlaceholders = false)){
        FavoritePagingSource(api, token, stateId)
    }.flow.cachedIn(viewModelScope)


    fun getFavorites(token: String) = viewModelScope.launch(Dispatchers.IO) {
        _getFavoritesCover.postValue(Resource.Loading)
        _getFavoritesCover.postValue(rezRepository.getFavorites(token))
    }


    fun addOrRemoveFavorites(
        id: String,
        token: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        _addOrRemoveFavoritesResponse.postValue( Resource.Loading)
        _addOrRemoveFavoritesResponse.postValue( rezRepository.addOrRemoveFavorites(id, token))
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

//    fun register( user: RegisterRequest
//    ) = viewModelScope.launch {
//        _registerResponse.value = Resource.Loading
//        _registerResponse.value = rezRepository.register(user)
//    }

    fun register( user : RegisterRequest) = viewModelScope.launch {
        _regResponse.value = Resource.Loading
        _regResponse.value = rezRepository.register(user)
    }


//    fun register(user: RegisterRequest) = viewModelScope.launch {
//        _registerResp.value = Resource.Loading
//        _registerResp.value  = rezRepository.register(user)
//    }

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
    ) = viewModelScope.launch{
        _changePasswordResponse.value = Resource.Loading
        _changePasswordResponse.value = rezRepository.changePassword(user, token)
    }

    fun complaints(user: ComplaintRequest,
                       token: String
    ) = viewModelScope.launch{
        _complaintResponse.value = Resource.Loading
        _complaintResponse.value = rezRepository.complaints(user, token)
    }

    fun clean(){
        _getVendorTableResponse.value = null
        _getTablesResponse.value = null
        _getOpeningHoursResponse.value = null
    }

    fun cleanImageProfile(){
        _uploadImageResponse.value = null
    }
    fun cleanMyProfile(){
        _getProfileResponse.value = null
    }
    fun cleanProfileResponse(){
        _updateProfileResponse.value = null
    }
    fun cleanTableResponse(){
        _bookTableResponse.value = null
    }
    fun cleanGetBookingResponse(){
        _eachBookingResponse.value = null
    }
    fun cleanProfileTableResponse(){
        _getProfileTableResponse.value = null
    }
    fun cleanMapResponse(){
        _getPlacesResponse.value = null
    }

    fun store(){
        _getHomeResponse.value = getHomeResponse.value
    }

}