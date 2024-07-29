package com.example.rickandmortyapplication.ui.locations

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapplication.data.database.entities.LocationEntity
import com.example.rickandmortyapplication.data.model.LocationDto
import com.example.rickandmortyapplication.data.repository.LocationRepository
import com.example.rickandmortyapplication.ui.filters.LocationFilterDialogFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class LocationsViewModel(
    application: Application,
    private val repository: LocationRepository
) : AndroidViewModel(application) {

    private val _locationUiState = MutableStateFlow<LocationUiState>(LocationUiState.Loading)
    val locationUiState: StateFlow<LocationUiState> = _locationUiState

    private val _searchQuery = MutableStateFlow("")
    private val _nameFilter = MutableStateFlow("")
    private val _typeFilter = MutableStateFlow("")
    private val _dimensionFilter = MutableStateFlow("")

    var searchQuery: StateFlow<String> = _searchQuery
    var nameFilter: StateFlow<String> = _nameFilter
    var typeFilter: StateFlow<String> = _typeFilter
    var dimensionFilter: StateFlow<String> = _dimensionFilter

    private var currentPage = 1
    private var isLastPage = false

    init {
        observeFiltersAndQuery()
    }

    private fun observeFiltersAndQuery() {
        viewModelScope.launch {
            combine(
                _nameFilter,
                _typeFilter,
                _dimensionFilter,
                _searchQuery
            ) { name, type, dimension, query ->
                Quad(name, type, dimension, query)
            }.collect { (name, type, dimension, query) ->
                currentPage = 1
                isLastPage = false
                loadLocations(name, type, dimension, query)
            }
        }
    }

    fun setFilters(name: String, type: String, dimension: String) {
        _nameFilter.value = name
        _typeFilter.value = type
        _dimensionFilter.value = dimension
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun loadLocations(name: String = _nameFilter.value,
                      type: String = _typeFilter.value,
                      dimension: String = _dimensionFilter.value,
                      query: String = _searchQuery.value) {
        viewModelScope.launch {
            _locationUiState.value = LocationUiState.Loading
            try {
                val locations = repository.getLocations(
                    page = currentPage,
                    name = name,
                    type = type,
                    dimension = dimension,
                    searchQuery = query,
                )
                _locationUiState.value = LocationUiState.Success(locations)
            } catch (e: Exception) {
                _locationUiState.value = LocationUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun loadNextPage() {
        if (!isLastPage) {
            currentPage++
            loadLocations()
        }
    }
}

data class Quad<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)