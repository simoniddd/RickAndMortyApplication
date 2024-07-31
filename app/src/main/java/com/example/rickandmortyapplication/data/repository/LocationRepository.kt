package com.example.rickandmortyapplication.data.repository

import com.example.rickandmortyapplication.data.database.LocationDao
import com.example.rickandmortyapplication.data.database.entities.LocationEntity
import com.example.rickandmortyapplication.data.model.CharacterDto
import com.example.rickandmortyapplication.data.model.LocationDto
import com.example.rickandmortyapplication.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
            val dbLocations = getAllCachedLocations().first()
            if (name.isBlank() && type.isBlank() && dimension.isBlank() && searchQuery.isBlank()) {
                val cachedLocations = getLocationsForPage(page, dbLocations)
                if (cachedLocations.isNotEmpty()) {
                    return@withContext cachedLocations
                } else {
                    fetchLocationsFromApi(page)
                }
            } else {
                filterLocationsInCache(name, type, dimension, searchQuery, dbLocations)
            }
        }
    }

    private fun getAllCachedLocations(): Flow<List<LocationEntity>> {
        return locationDao.getAllLocations()
    }

    private fun getLocationsForPage(
        page: Int,
        dbLocations: List<LocationEntity>
    ): List<LocationEntity> {
        return dbLocations.filter { it.page == page }
    }

    // Запрос данных из API и сохранение в базу данных
    private suspend fun fetchLocationsFromApi(page: Int): List<LocationEntity> {
        return try {
            val response = apiService.getAllLocations(page)
            if (response.isSuccessful && response.body() != null) {
                val locations = response.body()!!.results.map { locationResponse ->
                    LocationEntity(
                        locationResponse.id,
                        locationResponse.name,
                        locationResponse.type,
                        locationResponse.dimension,
                        locationResponse.residents,
                        page = page
                    )
                }
                locationDao.insertLocations(locations)
                locations
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Фильтрация данных в кэше по заданным критериям
    private fun filterLocationsInCache(
        name: String,
        type: String,
        dimension: String,
        searchQuery: String,
        dbLocations: List<LocationEntity>
    ): List<LocationEntity> {
        return dbLocations.filter { locationEntity ->
            (searchQuery.isBlank() || locationEntity.name.contains(
                searchQuery,
                ignoreCase = true
            )) &&
                    (name.isBlank() || locationEntity.name.contains(name, ignoreCase = true)) &&
                    (type.isBlank() || locationEntity.type.contains(type, ignoreCase = true)) &&
                    (dimension.isBlank() || locationEntity.dimension.contains(
                        dimension,
                        ignoreCase = true
                    ))
        }
    }

    suspend fun getLocationById(id: Int): LocationDto {
        return apiService.getLocation(id)
    }

    suspend fun getCharacterByUrl(url: String): CharacterDto {
        return apiService.getCharacterByUrl(url)
    }
}




