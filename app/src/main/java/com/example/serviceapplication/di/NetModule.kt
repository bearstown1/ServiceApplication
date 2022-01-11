package com.example.serviceapplication.di

import com.example.serviceapplication.data.net.OidcServerApi
import com.example.serviceapplication.data.net.repository.OidcServerRepository
import com.example.serviceapplication.data.net.repository.OidcServerRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetModule {

    @Singleton
    @Provides
    fun provideRetrofit() : Retrofit {
        val interceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder().apply {
            this.addInterceptor( interceptor)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout( 20, TimeUnit.SECONDS)
                .writeTimeout( 25, TimeUnit.SECONDS)
        }.build()

        return Retrofit.Builder()
            .addConverterFactory( GsonConverterFactory.create())
            .client( client)
            .baseUrl( "http://192.168.11.160:9091/")
            .build()
    }

    @Singleton
    @Provides
    fun provideOidcServerApi(retrofit: Retrofit) : OidcServerApi {
        return retrofit.create(OidcServerApi::class.java)
    }

    @Singleton
    @Provides
    fun provideOidcServerRepository( oidcServerApi:OidcServerApi): OidcServerRepository {
        return OidcServerRepositoryImpl( oidcServerApi)
    }

}