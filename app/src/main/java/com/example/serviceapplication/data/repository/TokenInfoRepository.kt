package com.example.serviceapplication.data.repository

import com.example.serviceapplication.data.model.TokenInfo
import com.example.serviceapplication.data.room.dao.TokenInfoDao
import com.example.serviceapplication.utils.logError
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TokenInfoRepository @Inject constructor(private val tokenInfoDao: TokenInfoDao){

    fun get(): Flow<TokenInfo> {
        return tokenInfoDao.get()
    }

    suspend fun insert( tokenInfo: TokenInfo) {
        try {
            tokenInfoDao.insert(tokenInfo)
        } catch( t:Throwable) {
            logError( t.message.toString())
        }
    }

    suspend fun delete( tokenInfo: TokenInfo) {
        try {
            tokenInfoDao.delete(tokenInfo)
        } catch( t:Throwable) {
            logError( t.message.toString())
        }
    }

}