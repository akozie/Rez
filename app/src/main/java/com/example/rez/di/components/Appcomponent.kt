package com.example.rez.di.components

import android.app.Application
import android.content.Context
import com.example.rez.di.module.AppModule
import com.example.rez.di.scopes.AppScope
import dagger.Component
import javax.inject.Singleton

@AppScope
@Component(modules = [AppModule::class])
interface Appcomponent {

    fun context(): Context
}