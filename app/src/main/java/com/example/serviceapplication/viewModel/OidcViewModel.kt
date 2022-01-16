package com.example.serviceapplication.viewModel

import android.content.Context
import android.webkit.URLUtil
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviceapplication.R
import com.example.serviceapplication.data.AppStatus
import com.example.serviceapplication.data.repository.DataStoreRepository
import com.example.serviceapplication.utils.log
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OidcViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    @ApplicationContext private val context: Context,
) :ViewModel(){

    private var _appStatus = MutableStateFlow(AppStatus.INIT)
    var appStatus: StateFlow<AppStatus> = _appStatus

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.readAppStatus.collect {
                _appStatus.value = AppStatus.valueOf( it)
            }
        }

        viewModelScope.launch( Dispatchers.IO) {
            dataStoreRepository.readOidcServerUrl.collect { it ->

                log( "dataStoreRepository.readOidcServerUrl : ${it}")

                if (it != null) {
                    oidcServerUrl.value = it
                }
            }
        }
    }

    val oidcServerUrl: MutableState<String> = mutableStateOf( "")

    // Setup ProgressBar ---------------------------------------------------------------------------
    val isShowProgressBar = mutableStateOf(false)

    // setup screen error --------------------------------------------------------------------------
    val setupErrorTitle: MutableState<String> = mutableStateOf( "")
    val setupErrorDesc: MutableState<String> = mutableStateOf( "")

    fun setSetupError( errorTitle:String, errorDescription: String) {
        setupErrorTitle.value = errorTitle
        setupErrorDesc.value = errorDescription
    }

    fun initSetupError( ) {
        setupErrorTitle.value = ""
        setupErrorDesc.value = ""
    }

    suspend fun saveOidcServerUrl() {
        withContext( Dispatchers.IO) {
            dataStoreRepository.persistOidcServerUrl( oidcServerUrl.value)
        }
    }

    fun updateOidcServerUrlAtScreen( newUrl: String) {
        oidcServerUrl.value = newUrl?.trim()
    }

    fun validateOidcServerUrl() : String? {
        if ( oidcServerUrl.value.isEmpty()) {
            return context.getString( R.string.error_server_url_empty)
        }

        if ( ! URLUtil.isValidUrl( oidcServerUrl.value)) {
            return context.getString( R.string.error_server_url_format)
        }

        return null
    }
    suspend fun changeAppStatus(appStatus: AppStatus) {
        withContext(Dispatchers.IO) {
            dataStoreRepository.persistAppStatus(appStatus = appStatus)
        }
    }

    // Main Content --------------------------------------------------------------------------------
    val mainErrorTitle: MutableState<String> = mutableStateOf( "")
    val mainErrorDesc: MutableState<String> = mutableStateOf( "")

    fun setMainError( errorTitle:String, errorDescription: String) {
        mainErrorTitle.value = errorTitle
        mainErrorTitle.value = errorDescription
    }

    fun initMainError( ) {
        mainErrorTitle.value = ""
        mainErrorDesc.value = ""
    }

    // snack bar -----------------------------------------------------------------------------------
    var showSnackBar : MutableState<Boolean> = mutableStateOf( false)

}