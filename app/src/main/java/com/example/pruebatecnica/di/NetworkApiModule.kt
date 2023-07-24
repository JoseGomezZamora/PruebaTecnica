package com.example.pruebatecnica.di

import com.example.pruebatecnica.data.network.UserLisApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkApiModule {

    //provveo a retrofit
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {

        return Retrofit.Builder()
            .baseUrl("https://api.devdicio.net:8444/v1/")
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(CustomHeaderInterceptor())
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }

    //con daggerHilt
    @Singleton
    @Provides
    fun provideUserListApiClient(retrofit: Retrofit): UserLisApiClient {

        return retrofit.create(UserLisApiClient::class.java)

    }

}