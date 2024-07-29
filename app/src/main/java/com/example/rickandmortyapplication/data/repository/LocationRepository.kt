package com.example.rickandmortyapplication.data.repository

import com.example.rickandmortyapplication.data.database.LocationDao
import com.example.rickandmortyapplication.data.database.entities.CharacterEntity
import com.example.rickandmortyapplication.data.database.entities.LocationEntity
import com.example.rickandmortyapplication.data.model.CharacterDto
import com.example.rickandmortyapplication.data.model.LocationDto
import com.example.rickandmortyapplication.data.model.toCharacterEntity
import com.example.rickandmortyapplication.data.model.toLocationDto
import com.example.rickandmortyapplication.data.model.toLocationEntity
import com.example.rickandmortyapplication.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class LocationRepository(
    private val apiService: ApiService,
    private val locationDao: LocationDao
) {
    suspend fun getLocations(
        page: Int,
        name: String = "",
        type: String = "",
        dimension: String = "",
        searchQuery: String = ""
    ): List<LocationEntity> {
        return withContext(Dispatchers.IO) {
            val dbLocations = locationDao.getAllLocations()
            if (name.isBlank() && type.isBlank() && dimension.isBlank() && searchQuery.isBlank()) {
                val cachedLocations = locationDao.getLocationsForPage(page)
                if (cachedLocations.isNotEmpty()) {
                    return@withContext cachedLocations
                } else {
                    try {
                        val response = apiService.getAllLocations(page)
                        if (response.isSuccessful && response.body() != null) {
                            val locations = response.body()!!.results.map { locationResponse ->
                                LocationEntity(
                                    locationResponse.id,
                                    locationResponse.name,
                                    locationResponse.type,
                                    locationResponse.dimension,
                                    locationResponse.residents
                                )
                            }
                            locationDao.insertLocations(locations)
                            return@withContext locations
                        } else {
                            return@withContext emptyList()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        return@withContext emptyList()
                    }
                }
            } else {
                return@withContext dbLocations
                    .map { locationList ->
                        locationList.filter { locationEntity ->
                            (searchQuery.isBlank() || locationEntity.name.contains(searchQuery, ignoreCase = true)) &&
                            (name.isBlank() || locationEntity.name.contains(name, ignoreCase = true)) &&
                                    (type.isBlank() || locationEntity.type.contains(type, ignoreCase = true)) &&
                                    (dimension.isBlank() || locationEntity.dimension.contains(dimension, ignoreCase = true))
                        }
                    }
                    .first()
            }
        }
    }

    suspend fun getLocationById(id: Int): LocationDto {
        // Получение локации из API
        return apiService.getLocation(id)
    }

    suspend fun getCharacterByUrl(url: String): CharacterDto {
        // Получение персонажа по URL из API
        return apiService.getCharacterByUrl(url)
    }
}



