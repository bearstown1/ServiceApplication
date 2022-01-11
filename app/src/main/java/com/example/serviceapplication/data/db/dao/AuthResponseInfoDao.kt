package com.example.serviceapplication.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.serviceapplication.data.model.AuthResponseInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthResponseInfoDao {
    @Query( "SELECT * FROM AuthResponseInfo WHERE id = 1")
    fun get(): Flow<AuthResponseInfo>

    @Insert
    suspend fun insert( oidcInfo: AuthResponseInfo)

    @Delete
    suspend fun delete( oidcInfo: AuthResponseInfo)
}