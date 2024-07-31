package com.example.rickandmortyapplication.ui.locations.details.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapplication.data.model.dto.toCharacterEntity
import com.example.rickandmortyapplication.data.repository.LocationRepository
import com.example.rickandmortyapplication.ui.locations.details.LocationDetailsUiState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LocationDetailsViewModel(
    private val locationRepository: LocationRepository
) : ViewModel() {
    private val _locationUiState =
        MutableStateFlow<LocationDetailsUiState>(LocationDetailsUiState.Loading)
    val locationUiState: StateFlow<LocationDetailsUiState> = _locationUiState

    fun getLocationDetails(locationId: Int) {
        viewModelScope.launch {
            _locationUiState.value = LocationDetailsUiState.Loading
            try {
                val locationDto = locationRepository.getLocationById(locationId)
                val charactersDeferred = locationDto.residents.map { url ->
                    async { locationRepository.getCharacterByUrl(url) }
                }
                val characters = charactersDeferred.awaitAll().map { it.toCharacterEntity() }
                _locationUiState.value = LocationDetailsUiState.Success(locationDto, characters)
            } catch (e: Exception) {
                _locationUiState.value = LocationDetailsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}