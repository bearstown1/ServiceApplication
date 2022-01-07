package com.example.serviceapplication.di

import com.example.serviceapplication.event.OidcEventBus
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class EventBusModule {

    @Provides
    @Singleton
    fun provideOidcEventBus(): OidcEventBus = OidcEventBus()
}