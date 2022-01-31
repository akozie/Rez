package com.example.rez.di.components

import com.example.rez.RezApp
import com.example.rez.di.module.LocalModule
import com.example.rez.di.scopes.ActivityScoped
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
    fun inject(fragment:RegistrationFragment)
    fun inject(dashboardActivity: TopFragment)
    fun inject(fragment : Favorites)
    fun inject(rezApplication: RezApp)
}