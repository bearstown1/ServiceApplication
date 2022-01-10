package com.example.serviceapplication.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.serviceapplication.data.AppStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore( name = DataStoreRepository.PREFERENCE_NAME)

class DataStoreRepository @Inject constructor(
    @ApplicationContext private val context : Context
) {
    private object PreferenceKeys {
        val appStatusKey = stringPreferencesKey(name = PREFERENCE_KEY_APP_STATUS)
    }

    private val dataStore = context.dataStore

    suspend fun persistAppStatus(appStatus: AppStatus) {
        dataStore.edit { preference ->
            preference[ PreferenceKeys.appStatusKey] = appStatus.name
        }
    }

    val readAppStatus: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val appStatus = preferences[ PreferenceKeys.appStatusKey] ?: AppStatus.SETUP.name
            appStatus
        }




    companion object {
        const val PREFERENCE_NAME               = "BANDI_OIDC_PREFERENCES"
        const val PREFERENCE_KEY_APP_STATUS     = "KEY_APP_STATUS"
    }

}