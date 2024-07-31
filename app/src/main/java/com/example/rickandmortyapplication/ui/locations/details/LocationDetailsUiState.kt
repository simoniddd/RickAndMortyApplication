package com.example.rickandmortyapplication.ui.locations.details

import com.example.rickandmortyapplication.data.database.entities.CharacterEntity
import com.example.rickandmortyapplication.data.model.dto.LocationDto

sealed class LocationDetailsUiState {
    object Loading : LocationDetailsUiState()
    data class Success(val location: LocationDto, val characters: List<CharacterEntity>) :
        LocationDetailsUiState()

    data class Error(val message: String) : LocationDetailsUiState()
}