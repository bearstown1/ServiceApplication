package com.example.serviceapplication.di

import com.example.serviceapplication.data.repository.AuthResponseInfoRepository
import com.example.serviceapplication.data.db.OidcDatabase
import com.example.serviceapplication.data.db.dao.AuthResponseInfoDao
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
    fun provideTokenInfoDao( database: OidcDatabase): AuthResponseInfoDao {
        return database.tokenInfoDao()
    }

    @Provides
    @Singleton
    fun provideTokenInfoRepository(authResponseInfoDao: AuthResponseInfoDao): AuthResponseInfoRepository {
        return AuthResponseInfoRepository( authResponseInfoDao)
    }
}