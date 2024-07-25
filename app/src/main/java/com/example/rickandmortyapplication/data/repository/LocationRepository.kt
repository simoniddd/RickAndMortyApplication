package com.example.rickandmortyapplication.data.repository

import com.example.rickandmortyapplication.data.database.LocationDao
import com.example.rickandmortyapplication.data.database.entities.LocationEntity
import com.example.rickandmortyapplication.data.network.ApiService
import com.example.rickandmortyapplication.data.network.RetrofitInstance.api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
    dimension: String = ""
    ): List<LocationEntity> {
        return withContext(Dispatchers.IO) {
            val dbLocations = locationDao.getAllLocations()
            if (name.isBlank() && type.isBlank() && dimension.isBlank()) {
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
                                locationResponse.name,locationResponse.type,
                                locationResponse.dimension,
                                page
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
                        (name.isBlank() || locationEntity.name.contains(name, ignoreCase = true)) &&
                                (type.isBlank() || locationEntity.type.contains(type, ignoreCase = true)) &&
                                (dimension.isBlank() || locationEntity.dimension.contains(dimension, ignoreCase = true))
                    }
                }
                .first()
            }
        }
    }

    fun getLocationById(id: Int): Flow<LocationEntity> {
        return locationDao.getLocationById(id)
    }
}
