package com.example.serviceapplication.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviceapplication.data.AppStatus
import com.example.serviceapplication.data.repository.DataStoreRepository
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
    }

    suspend fun changeAppStatus(appStatus: AppStatus) {
        withContext(Dispatchers.IO) {
            dataStoreRepository.persistAppStatus(appStatus = appStatus)
        }
    }

}