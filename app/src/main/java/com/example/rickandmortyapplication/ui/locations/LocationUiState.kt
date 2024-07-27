package com.example.rickandmortyapplication.ui.locations

import com.example.rickandmortyapplication.data.database.entities.LocationEntity

sealed class LocationUiState {
    object Loading : LocationUiState()
    data class Success(val locations: List<LocationEntity>) : LocationUiState()
    data class Error(val message: String) : LocationUiState()
}