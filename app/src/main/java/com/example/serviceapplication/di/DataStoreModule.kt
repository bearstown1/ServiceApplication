package com.example.serviceapplication.di

import android.content.Context
import com.example.serviceapplication.data.repository.DataStoreRepository
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {

    fun provideDataStoreRepository(@ApplicationContext appContext: Context): DataStoreRepository {
        return DataStoreRepository(appContext)
    }
}