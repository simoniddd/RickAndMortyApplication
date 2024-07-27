package com.example.rickandmortyapplication.data.network

import com.example.rickandmortyapplication.data.model.CharacterResponse
import com.example.rickandmortyapplication.data.database.entities.EpisodeEntity
import com.example.rickandmortyapplication.data.model.CharacterDto
import com.example.rickandmortyapplication.data.model.EpisodeDTO
import com.example.rickandmortyapplication.data.model.EpisodeResponse
import com.example.rickandmortyapplication.data.model.LocationDto
import com.example.rickandmortyapplication.data.model.LocationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    //запрос на получение листа всех персонажей
    @GET("character")
    suspend fun getAllCharacters(@Query("page") page: Int): Response<CharacterResponse>

    //запрос на получение листа всех эпизодов
    @GET("episode")
    suspend fun getAllEpisodes(@Query("page") page: Int): Response<EpisodeResponse>

    @GET("episode/{id}")
    suspend fun getEpisode(@Path("id") id: Int): EpisodeDTO

    @GET
    suspend fun getEpisodeByUrl(@Url url: String): EpisodeDTO

    @GET
    suspend fun getCharacterByUrl(@Url url: String): CharacterDto

    @GET("character/{id}")
    suspend fun getCharacterById(@Path("id") id: Int): CharacterDto

    //запрос на получение листа всех локаций
    @GET("location")
    suspend fun getAllLocations(@Query("page") page: Int): Response<LocationResponse>

    @GET("location/{id}")
    suspend fun getLocation(@Path("id") id: Int): LocationDto
}