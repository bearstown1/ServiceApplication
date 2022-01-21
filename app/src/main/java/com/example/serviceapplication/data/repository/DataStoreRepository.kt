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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore( name = DataStoreRepository.PREFERENCE_NAME)

class DataStoreRepository @Inject constructor(
    @ApplicationContext private val context : Context
) {
    private object PreferenceKeys {
        val appStatusKey = stringPreferencesKey(name = PREFERENCE_KEY_APP_STATUS)
        val oidcServerUrlKey = stringPreferencesKey( name = PREFERENCE_KEY_OIDC_SERVER_URL)
        val metaDataKey = stringPreferencesKey( name = PREFERENCE_KEY_METADATA)
        val userKey = stringPreferencesKey(name = PREFERENCE_KEY_USER)
    }

    private val dataStore = context.dataStore

    suspend fun persistUserInfo( json: String) {
        withContext( Dispatchers.IO) {
            dataStore.edit { preference ->
                preference[PreferenceKeys.userKey] = json
            }
        }
    }

    val readUserInfo: Flow<String?> = dataStore.data
        .catch { exception ->
            if( exception is IOException) {
                emit( emptyPreferences())
            } else{
                throw exception
            }
        }
        .map { preferences ->
            preferences[ PreferenceKeys.userKey] ?: null
        }

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
            val appStatus = preferences[ PreferenceKeys.appStatusKey] ?: AppStatus.INIT.name
            appStatus
        }

    suspend fun persistOidcServerUrl( url: String) {
        dataStore.edit { preference ->
            preference[ PreferenceKeys.oidcServerUrlKey] = url
        }
    }

    val readOidcServerUrl: Flow<String?> = dataStore.data
        .catch { exception ->
            if( exception is IOException) {
                emit( emptyPreferences())
            } else{
                throw exception
            }
        }
        .map { preferences ->
            preferences[ PreferenceKeys.oidcServerUrlKey] ?: null
        }




    suspend fun persistMetadata( json: String) {
        withContext( Dispatchers.IO) {
            dataStore.edit { preference ->
                preference[PreferenceKeys.metaDataKey] = json
            }
        }
    }

    val readMetadata: Flow<String?> = dataStore.data
        .catch { exception ->
            if( exception is IOException) {
                emit( emptyPreferences())
            } else{
                throw exception
            }
        }
        .map { preferences ->
            preferences[ PreferenceKeys.metaDataKey] ?: null
        }

    companion object {
        const val PREFERENCE_NAME               = "BANDI_OIDC_PREFERENCES"
        const val PREFERENCE_KEY_APP_STATUS     = "KEY_APP_STATUS"
        const val PREFERENCE_KEY_OIDC_SERVER_URL    = "KEY_OIDC_SERVER_URL"
        const val PREFERENCE_KEY_METADATA           = "KEY_METADATA"
        const val PREFERENCE_KEY_USER = "KEY_USER"
    }

}