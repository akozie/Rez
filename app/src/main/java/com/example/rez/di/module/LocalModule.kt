package com.example.rez.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.rez.di.scopes.ActivityScoped
import dagger.Module
import dagger.Provides


@Module
class LocalModule {


    @Provides
    @ActivityScoped
    fun providesSharedPreferences(context: Context):SharedPreferences =
        context.getSharedPreferences("SharedPref", Context.MODE_PRIVATE)


}