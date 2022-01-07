package com.example.rez.di.module

import android.app.Application
import android.content.Context
import com.example.rez.di.scopes.AppScope
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class AppModule(private val context: Context) {

    @Provides
    @AppScope
    fun providesApplicationContext(): Context = context
}