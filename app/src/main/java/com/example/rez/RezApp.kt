package com.example.rez

import android.app.Application
import android.content.SharedPreferences
import com.example.rez.di.components.Appcomponent
import com.example.rez.di.components.DaggerAppcomponent
import com.example.rez.di.components.DaggerLocalComponent
import com.example.rez.di.components.LocalComponent
import com.example.rez.di.module.AppModule
import com.example.rez.di.module.LocalModule
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import javax.inject.Inject

class RezApp: Application() {


    var localComponent: LocalComponent? = null
    var appcomponent: Appcomponent? = null

    @Inject
    lateinit var sharedPreference: SharedPreferences



    override fun onCreate() {
        super.onCreate()

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)

        appcomponent = DaggerAppcomponent.builder()
            .appModule(AppModule(this))
            .build()


        localComponent = DaggerLocalComponent.builder()
            .appcomponent(appcomponent)
            .localModule(LocalModule())
            .build()
        localComponent?.inject(this)
    }
}