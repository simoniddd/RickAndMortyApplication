package com.example.myapp.ui.locations

import LocationRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapplication.data.database.entities.LocationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class LocationViewModel(
    application: Application,
    private val repository: LocationRepository
) : AndroidViewModel(application) {

    val allLocations: Flow<List<LocationEntity>> = repository.getAllLocations()

    fun refreshLocations(page: Int) {
        viewModelScope.launch {
            repository.refreshLocations(page)
        }
    }
}
