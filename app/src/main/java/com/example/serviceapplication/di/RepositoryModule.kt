package com.example.serviceapplication.di

import com.example.serviceapplication.data.repository.TokenInfoRepository
import com.example.serviceapplication.data.room.OidcDatabase
import com.example.serviceapplication.data.room.dao.TokenInfoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn( SingletonComponent::class)
class RepositoryModule {
    @Provides
    @Singleton
    fun provideTokenInfoDao( database: OidcDatabase): TokenInfoDao {
        return database.tokenInfoDao()
    }

    @Provides
    @Singleton
    fun provideTokenInfoRepository( tokenInfoDao: TokenInfoDao): TokenInfoRepository {
        return TokenInfoRepository( tokenInfoDao)
    }
}