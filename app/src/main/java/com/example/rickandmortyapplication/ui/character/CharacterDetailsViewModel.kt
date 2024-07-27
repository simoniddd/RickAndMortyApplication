package com.example.rickandmortyapplication.ui.character

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapplication.data.model.toEpisodeEntity
import com.example.rickandmortyapplication.data.repository.CharacterRepository
import com.example.rickandmortyapplication.data.repository.EpisodeRepository
import com.example.rickandmortyapplication.ui.episodes.EpisodeUiState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CharacterDetailsViewModel(
    private val characterRepository: CharacterRepository
) : ViewModel() {

    private val _characterUiState = MutableStateFlow<CharacterDetailsUiState>(CharacterDetailsUiState.Loading)
    val characterUiState: StateFlow<CharacterDetailsUiState> = _characterUiState

    private val _episodesUiState = MutableStateFlow<EpisodeUiState>(EpisodeUiState.Loading)
    val episodesUiState: StateFlow<EpisodeUiState> = _episodesUiState

    fun getCharacterDetails(characterId: Int) {
        viewModelScope.launch {
            _characterUiState.value = CharacterDetailsUiState.Loading
            _episodesUiState.value = EpisodeUiState.Loading
            try {
                // Fetch character details
                val characterDto = characterRepository.getCharacterById(characterId)

                // Fetch episodes related to the character
                val episodesDeferred = characterDto.episode.map { url ->
                    async { characterRepository.getEpisodeByUrl(url) }
                }

                // Await results of all episode fetches
                val episodes = episodesDeferred.awaitAll().map { it.toEpisodeEntity() }

                _characterUiState.value = CharacterDetailsUiState.Success(characterDto, episodes)
                _episodesUiState.value = EpisodeUiState.Success(episodes)
            } catch (e: Exception) {
                _characterUiState.value = CharacterDetailsUiState.Error(e.message ?: "Unknown error")
                _episodesUiState.value = EpisodeUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
