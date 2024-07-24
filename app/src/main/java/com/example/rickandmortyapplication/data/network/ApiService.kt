package com.example.rickandmortyapplication.data.network

import com.example.rickandmortyapplication.data.model.CharacterResponse
import com.example.rickandmortyapplication.data.database.entities.EpisodeEntity
import com.example.rickandmortyapplication.data.model.EpisodeResponse
import com.example.rickandmortyapplication.data.model.LocationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    //запрос на получение листа всех персонажей
    @GET("character")
    suspend fun getAllCharacters(@Query("page") page: Int): Response<CharacterResponse>

    //запрос на получение листа всех эпизодов
    @GET("episode")
    suspend fun getAllEpisodes(@Query("page") page: Int): EpisodeResponse

    @GET
    suspend fun getEpisode(@Url episodeUrl: String): EpisodeEntity

    //запрос на получение листа всех локаций
    @GET("location")
    suspend fun getAllLocations(@Query("page") page: Int): LocationResponse
}