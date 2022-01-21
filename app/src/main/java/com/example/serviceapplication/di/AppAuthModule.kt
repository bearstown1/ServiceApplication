package com.example.serviceapplication.di

import android.content.Context
import com.example.serviceapplication.data.repository.AuthResponseInfoRepository
import com.example.serviceapplication.data.repository.DataStoreRepository
import com.example.serviceapplication.appauth.OidcHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.openid.appauth.AuthorizationService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppAuthModule {

    @Singleton
    @Provides
    fun provideAuthorizationService( @ApplicationContext context : Context): AuthorizationService {
        return AuthorizationService( context)
    }

    @Singleton
    @Provides
    fun provideOidcHandler (authResponseInfoRepository: AuthResponseInfoRepository, dataStoreRepository: DataStoreRepository, authorizationService: AuthorizationService): OidcHandler {
        return OidcHandler(authResponseInfoRepository, dataStoreRepository, authorizationService)
    }

}