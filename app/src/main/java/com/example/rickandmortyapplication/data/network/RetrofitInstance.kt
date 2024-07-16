package com.example.rickandmortyapplication.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

    object RetrofitInstance {
        private const val BASE_URL = "https://rickandmortyapi.com/api/"

        // Создаём интерцептор для логирования (HTTP запросы и ответы)
        private val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Создаём HTTP клиент с логами
        private val httpClient = OkHttpClient.Builder().apply {
            addInterceptor(loggingInterceptor)
        }.build()

        // Создаём экземпляр Retrofit с базовым URL и настройками

        val api: ApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()
                .create(ApiService::class.java)
        }
}