package com.example.rez.util

import android.content.Context
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileImageDataStoreManager(context: Context) {

    // Create the dataStore and give it a name same as shared preferences

    private val mDataStore: DataStore<Preferences> = context.dataStore
    // Create some keys we will use them to store and retrieve the data
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_pref")
        val DOWNLOAD_URL = stringPreferencesKey("DOWNLOAD_URL")
    }

    // Store user data
    // refer to the data store and using edit
    // we can store values using the keys

    suspend fun storeProfileImageUrl(downloadUrl: Uri) {

        mDataStore.edit { preferences ->

            preferences[DOWNLOAD_URL] = downloadUrl.toString()

            // here it refers to the preferences we are editing
        }
    }

    // Create an age flow to retrieve download url from the preferences
    // flow comes from the kotlin coroutine

    val profileImageFlow: Flow<String> = mDataStore.data.map {
        it[DOWNLOAD_URL] ?: ""
    }
}
