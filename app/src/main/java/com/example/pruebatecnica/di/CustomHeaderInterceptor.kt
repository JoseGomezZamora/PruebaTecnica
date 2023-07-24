package com.example.pruebatecnica.di

import okhttp3.Interceptor
import okhttp3.Response

class CustomHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("Host", "api.devdicio.net")
            .addHeader("xc-token", "J38b4XQNLErVatKIh4oP1jw9e_wYWkS86Y04TMNP")
            .build()

        return chain.proceed(request)
    }

}