package com.example.rickandmortyapplication.ui.locations.detailLocation

import LocationRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapplication.data.database.entities.LocationEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LocationDetailsViewModel(
    application: Application,
    private val repository: LocationRepository
) : AndroidViewModel(application) {

    private val _location = MutableStateFlow<LocationEntity?>(null)
    val location: StateFlow<LocationEntity?> = _location

    fun getLocationById(locationId: Int) {
        viewModelScope.launch {
            repository.getLocationById(locationId)
                .collect { location ->
                    _location.value = location
                }
        }
    }
}
