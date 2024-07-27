package com.example.rickandmortyapplication.ui.episodes


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapplication.data.model.toCharacterEntity
import com.example.rickandmortyapplication.data.repository.EpisodeRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EpisodeDetailsViewModel(
    private val episodeRepository: EpisodeRepository
) : ViewModel() {

    private val _episodeUiState = MutableStateFlow<EpisodeDetailsUiState>(EpisodeDetailsUiState.Loading)
    val episodeUiState: StateFlow<EpisodeDetailsUiState> = _episodeUiState

    fun getEpisodeDetails(episodeId: Int) {
        viewModelScope.launch {
            _episodeUiState.value = EpisodeDetailsUiState.Loading
            try {
                // Fetch episode details
                val episodeDto = episodeRepository.getEpisodeById(episodeId)

                // Use async to fetch characters in parallel
                val charactersDeferred = episodeDto.characters.map { url ->
                    async { episodeRepository.getCharacterByUrl(url) }
                }

                // Await results of all character fetches
                val characters = charactersDeferred.awaitAll().map { it.toCharacterEntity() }

                _episodeUiState.value = EpisodeDetailsUiState.Success(episodeDto, characters)
            } catch (e: Exception) {
                _episodeUiState.value = EpisodeDetailsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}