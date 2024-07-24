package com.example.rickandmortyapplication.data.repository

import com.example.rickandmortyapplication.data.database.LocationDao
import com.example.rickandmortyapplication.data.database.entities.LocationEntity
import com.example.rickandmortyapplication.data.network.ApiService
import com.example.rickandmortyapplication.data.network.RetrofitInstance.api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class LocationRepository(
    private val apiService: ApiService,
    private val locationDao: LocationDao
) {
    suspend fun getLocations(page: Int, query: String = ""): List<LocationEntity> {
        return withContext(Dispatchers.IO) {
            val cachedLocations = locationDao.getLocationsForPage(page)
            if (cachedLocations.isNotEmpty() && query.isBlank()) {
                cachedLocations
            } else {
                val locations = try {
                    val response = apiService.getAllLocations(page)
                    response.results.map {
                        LocationEntity(it.id, it.name, it.type, it.dimension, page) // Add page
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    emptyList()
                }

                val filteredLocations = if (query.isNotBlank()) {
                    locations.filter { it.name.contains(query, ignoreCase = true) }
                } else {
                    locations}

                locationDao.insertLocations(filteredLocations)
                filteredLocations
            }
        }
    }

    fun getLocationById(id: Int): Flow<LocationEntity> {
        return locationDao.getLocationById(id)
    }
}
