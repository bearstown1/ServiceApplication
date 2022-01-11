package com.example.serviceapplication.data.repository

import com.example.serviceapplication.data.model.AuthResponseInfo
import com.example.serviceapplication.data.db.dao.AuthResponseInfoDao
import com.example.serviceapplication.utils.logError
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthResponseInfoRepository @Inject constructor(private val authResponseInfoDao: AuthResponseInfoDao){

    fun get(): Flow<AuthResponseInfo> {
        return authResponseInfoDao.get()
    }

    suspend fun insert(authResponseInfo: AuthResponseInfo) {
        try {
            authResponseInfoDao.insert(authResponseInfo)
        } catch( t:Throwable) {
            logError( t.message.toString())
        }
    }

    suspend fun delete(authResponseInfo: AuthResponseInfo) {
        try {
            authResponseInfoDao.delete(authResponseInfo)
        } catch( t:Throwable) {
            logError( t.message.toString())
        }
    }

}