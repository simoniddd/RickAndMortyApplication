package com.example.rickandmortyapplication.data.network

import com.example.rickandmortyapplication.data.model.responses.CharacterResponse
import com.example.rickandmortyapplication.data.model.dto.CharacterDto
import com.example.rickandmortyapplication.data.model.dto.EpisodeDTO
import com.example.rickandmortyapplication.data.model.responses.EpisodeResponse
import com.example.rickandmortyapplication.data.model.dto.LocationDto
import com.example.rickandmortyapplication.data.model.responses.LocationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    @GET("character")
    suspend fun getAllCharacters(@Query("page") page: Int): Response<CharacterResponse>

    @GET
    suspend fun getCharacterByUrl(@Url url: String): CharacterDto

    @GET("character/{id}")
    suspend fun getCharacterById(@Path("id") id: Int): CharacterDto

    @GET("episode")
    suspend fun getAllEpisodes(@Query("page") page: Int): Response<EpisodeResponse>

    @GET("episode/{id}")
    suspend fun getEpisode(@Path("id") id: Int): EpisodeDTO

    @GET
    suspend fun getEpisodeByUrl(@Url url: String): EpisodeDTO


    @GET("location")
    suspend fun getAllLocations(@Query("page") page: Int): Response<LocationResponse>

    @GET("location/{id}")
    suspend fun getLocation(@Path("id") id: Int): LocationDto
}