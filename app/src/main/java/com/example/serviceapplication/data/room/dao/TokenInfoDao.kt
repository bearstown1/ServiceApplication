package com.example.serviceapplication.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.serviceapplication.data.model.TokenInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface TokenInfoDao {
    @Query( "SELECT * FROM TokenInfo WHERE id = 1")
    fun get(): Flow<TokenInfo>

    @Insert
    suspend fun insert( oidcInfo: TokenInfo)

    @Delete
    suspend fun delete( oidcInfo: TokenInfo)
}