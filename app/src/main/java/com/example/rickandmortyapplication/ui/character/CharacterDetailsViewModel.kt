package com.example.rickandmortyapplication.ui.character

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapplication.data.model.toEpisodeEntity
import com.example.rickandmortyapplication.data.repository.CharacterRepository
import com.example.rickandmortyapplication.ui.episodes.EpisodeUiState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CharacterDetailsViewModel(
    private val characterRepository: CharacterRepository
) : ViewModel() {

    private val _characterUiState =
        MutableStateFlow<CharacterDetailsUiState>(CharacterDetailsUiState.Loading)
    val characterUiState: StateFlow<CharacterDetailsUiState> = _characterUiState

    private val _episodesUiState = MutableStateFlow<EpisodeUiState>(EpisodeUiState.Loading)

    fun getCharacterDetails(characterId: Int) {
        viewModelScope.launch {
            _characterUiState.value = CharacterDetailsUiState.Loading
            _episodesUiState.value = EpisodeUiState.Loading
            try {
                val characterDto = characterRepository.getCharacterById(characterId)
                val episodesDeferred = characterDto.episode.map { url ->
                    async { characterRepository.getEpisodeByUrl(url) }
                }
                val episodes = episodesDeferred.awaitAll().map { it.toEpisodeEntity() }
                _characterUiState.value = CharacterDetailsUiState.Success(characterDto, episodes)
                _episodesUiState.value = EpisodeUiState.Success(episodes)
            } catch (e: Exception) {
                _characterUiState.value =
                    CharacterDetailsUiState.Error(e.message ?: "Unknown error")
                _episodesUiState.value = EpisodeUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
