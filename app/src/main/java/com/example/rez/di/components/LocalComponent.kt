package com.example.rez.di.components

import com.example.rez.RezApp
import com.example.rez.di.module.LocalModule
import com.example.rez.di.scopes.ActivityScoped
import com.example.rez.ui.activity.DashboardActivity
import com.example.rez.ui.activity.MainActivity
import com.example.rez.ui.fragment.authentication.ForgotPasswordFragment
import com.example.rez.ui.fragment.authentication.LoginFragment
import com.example.rez.ui.fragment.authentication.RegistrationFragment
import com.example.rez.ui.fragment.authentication.ResetPasswordFragment
import com.example.rez.ui.fragment.dashboard.*
import dagger.Component

@ActivityScoped
@Component(modules = [LocalModule::class],dependencies = [Appcomponent::class])
interface LocalComponent {

    fun inject(fragment: LoginFragment)
    fun inject(fragment:MyProfile)
    fun inject(fragment:ChangePassword)
    fun inject(fragment:Home)
    fun inject(fragment:ResetPasswordFragment)
    fun inject(fragment:ForgotPasswordFragment)
    fun inject(fragment:RegistrationFragment)
    fun inject(fragment:TopRecommended)
    fun inject(fragment: TopFragment)
    fun inject(fragment: SuggestionForYou)
    fun inject(fragment: NearRestaurant)
    fun inject(fragment: NearRestFragment)
    fun inject(fragment: SuggestFragment)
    fun inject(fragment: TableDetails)
    fun inject(fragment: AboutFragment)
    fun inject(fragment: ReviewFragment)
    fun inject(fragment: Search)
    fun inject(fragment: SearchFragment)
    fun inject(fragment: BookingHistory)
    fun inject(fragment: BookingDetailsFragment)
    fun inject(fragment: ProceedToPayment)
    fun inject(fragment: FavoriteDetailsFragment)
    fun inject(fragment: Reservation)
    fun inject(fragment: QRCodeFragment)
    fun inject(fragment: FavoritesCover)
    fun inject(activity: MainActivity)
    fun inject(activity: DashboardActivity)
    fun inject(fragment : Favorites)
    fun inject(fragment : NotificationFragment)
    fun inject(fragment : ComplaintsFragment)
    fun inject(fragment : OpeningHoursFragment)
    fun inject(rezApplication: RezApp)
}