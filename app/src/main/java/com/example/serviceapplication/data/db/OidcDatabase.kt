package com.example.serviceapplication.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.serviceapplication.data.model.AuthResponseInfo
import com.example.serviceapplication.data.db.dao.AuthResponseInfoDao

@Database(entities = [ AuthResponseInfo::class], version = 7, exportSchema = false)
abstract class OidcDatabase: RoomDatabase() {

    abstract fun tokenInfoDao(): AuthResponseInfoDao
}