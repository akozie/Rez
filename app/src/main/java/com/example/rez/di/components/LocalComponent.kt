package com.example.rez.di.components

import com.example.rez.RezApp
import com.example.rez.api.RemoteDataSource
import com.example.rez.di.module.LocalModule
import com.example.rez.di.scopes.ActivityScoped
import com.example.rez.ui.fragment.authentication.LoginFragment
import com.example.rez.ui.fragment.dashboard.ChangePassword
import com.example.rez.ui.fragment.dashboard.Home
import com.example.rez.ui.fragment.dashboard.MyProfile
import dagger.Component

@ActivityScoped
@Component(modules = [LocalModule::class],dependencies = [Appcomponent::class])
interface LocalComponent {

    fun inject(fragment: LoginFragment)
    fun inject(fragment:MyProfile)
    fun inject(fragment:ChangePassword)
    fun inject(fragment:Home)
//    fun inject(fragment:BusinessAccountFragment)
//    fun inject(dashboardActivity: DashboardActivity)
    fun inject(rezApplication: RezApp)
}