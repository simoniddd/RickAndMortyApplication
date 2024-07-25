package com.example.rickandmortyapplication.ui.locations

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapplication.data.database.entities.LocationEntity
import com.example.rickandmortyapplication.data.repository.LocationRepository
import com.example.rickandmortyapplication.ui.filters.LocationFilterDialogFragment
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

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var currentPage = 1
    private var isLastPage = false

    init {
        loadLocations()
    }

    private val _nameFilter = MutableStateFlow("")
    private val _typeFilter = MutableStateFlow("")
    private val _dimensionFilter = MutableStateFlow("")

    fun applyFilters(filters: LocationFilterDialogFragment.LocationFilterData) {
        _nameFilter.value = filters.name
        _typeFilter.value = filters.type
        _dimensionFilter.value = filters.dimension
        currentPage= 1
        isLastPage = false
        loadLocations()
    }

    fun loadLocations(query: String = "") {
        viewModelScope.launch {
            _locationUiState.value = LocationUiState.Loading
            try {
                val locations = repository.getLocations(
                    page = currentPage,
                    name = _nameFilter.value,
                    type = _typeFilter.value,
                    dimension = _dimensionFilter.value
                )
                _locationUiState.value = LocationUiState.Success(locations)
                isLastPage = locations.isEmpty()
                if (!isLastPage) {
                    currentPage++
                }
            } catch (e: Exception) {
                _locationUiState.value = LocationUiState.Error("Failed to load locations")
            }
        }
    }

    fun loadNextPage() {
        if (!isLastPage && searchQuery.value.isBlank()) { // Only load next page if not searching
            loadLocations()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
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