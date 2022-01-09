package com.example.serviceapplication.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.serviceapplication.data.model.TokenInfo
import com.example.serviceapplication.data.room.dao.TokenInfoDao

@Database(entities = [ TokenInfo::class], version = 1, exportSchema = false)
abstract class OidcDatabase: RoomDatabase() {

    abstract fun tokenInfoDao(): TokenInfoDao
}