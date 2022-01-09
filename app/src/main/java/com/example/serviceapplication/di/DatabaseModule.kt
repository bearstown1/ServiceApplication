package com.example.serviceapplication.di

import android.content.Context
import androidx.room.Room
import com.example.serviceapplication.data.room.OidcDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn( SingletonComponent::class)
class DatabaseModule {
    @Singleton
    @Provides
    fun provideSearchDatabase( @ApplicationContext context : Context) =
        Room.databaseBuilder( context, OidcDatabase::class.java, "OidcDatabase")
            .fallbackToDestructiveMigration()
            .build()

}