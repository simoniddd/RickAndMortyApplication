package com.example.rickandmortyapplication.ui.locations

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapplication.data.database.entities.LocationEntity
import com.example.rickandmortyapplication.data.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class LocationsViewModel(
    application: Application,
    private val repository: LocationRepository
) : AndroidViewModel(application) {private val _locationUiState = MutableStateFlow<LocationUiState>(LocationUiState.Loading)
    val locationUiState: StateFlow<LocationUiState> = _locationUiState.asStateFlow()

    private var currentPage = 1
    var isLastPage = false
    private var currentQuery = ""

    init {
        loadLocations()
    }

    fun loadLocations(query: String = "") {
        viewModelScope.launch {
            _locationUiState.value = LocationUiState.Loading
            try {
                val locations = repository.getLocations(currentPage, query)
                _locationUiState.value = LocationUiState.Success(locations)
                isLastPage = locations.isEmpty()
                currentPage++
            } catch (e: Exception) {
                _locationUiState.value = LocationUiState.Error("Failed to loadlocations")
            }
        }
    }

    fun loadNextPage() {
        if (!isLastPage) {
            loadLocations(currentQuery)
        }
    }

    fun setSearchQuery(query: String) {
        currentQuery = query
        currentPage = 1
        isLastPage = false
        loadLocations(query)
    }

    fun getLocationById(id: Int): Flow<LocationEntity> {
        return repository.getLocationById(id)
    }
}

sealed class LocationUiState {
    object Loading : LocationUiState()
    data class Success(val locations: List<LocationEntity>) : LocationUiState()
    data class Error(val message: String) : LocationUiState()
}