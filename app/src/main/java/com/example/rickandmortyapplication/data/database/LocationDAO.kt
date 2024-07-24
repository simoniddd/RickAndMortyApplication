package com.example.rickandmortyapplication.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rickandmortyapplication.data.database.entities.LocationEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface LocationDao {
    @Query("SELECT * FROM locations")
    fun getAllLocations(): Flow<List<LocationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocations(locations: List<LocationEntity>)

    @Query("SELECT * FROM locations WHERE id = :locationId")
    fun getLocationById(locationId: Int): Flow<LocationEntity>

    @Query("SELECT * FROM locations WHERE name LIKE '%' || :query || '%'")
    fun getFilteredLocations(query: String): Flow<List<LocationEntity>>

    @Query("SELECT* FROM locations WHERE page = :page")
    suspend fun getLocationsForPage(page: Int): List<LocationEntity>
}