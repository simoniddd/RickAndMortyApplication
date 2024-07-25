package com.example.rickandmortyapplication.ui.character

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapplication.data.database.entities.CharacterEntity
import com.example.rickandmortyapplication.data.database.entities.EpisodeEntity
import com.example.rickandmortyapplication.data.network.RetrofitInstance.api
import com.example.rickandmortyapplication.data.repository.CharacterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CharacterViewModel(
    application: Application,
    private val repository: CharacterRepository
) : AndroidViewModel(application) {private val _characterUiState = MutableStateFlow<CharacterUiState>(CharacterUiState.Loading)
    val characterUiState: StateFlow<CharacterUiState> = _characterUiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var currentPage = 1
    private var isLastPage = false

    init {
        loadCharacters()
    }

    fun loadCharacters(query: String = "") {
        viewModelScope.launch {
            _characterUiState.value = CharacterUiState.Loading
            try {
                val characters = repository.getCharacters(currentPage, query)
                _characterUiState.value = CharacterUiState.Success(characters)
                isLastPage = characters.isEmpty()
                if (!isLastPage){
                    currentPage++
                }
            } catch (e: Exception) {
                _characterUiState.value = CharacterUiState.Error("Failed to load characters")
            }
        }
    }

    fun loadNextPage() {
        if (!isLastPage && searchQuery.value.isBlank()) {
            loadCharacters()
        }
    }

    fun refreshCharacters() {
        viewModelScope.launch {
            _characterUiState.value = CharacterUiState.Loading
            try {
                val characters = repository.refreshCharacters(currentPage)
                _characterUiState.value = CharacterUiState.Success(characters)
            } catch (e: Exception) {
                _characterUiState.value = CharacterUiState.Error("Failed to refresh characters")
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        currentPage = 1
        isLastPage = false
        loadCharacters(query)
    }

    fun getCharacter(characterId: Int): Flow<CharacterEntity> {
        return repository.getCharacterById(characterId)}

    suspend fun getEpisodeData(episodeUrl: String): EpisodeEntity {
        return withContext(Dispatchers.IO) {
            api.getEpisode(episodeUrl)
        }
    }
}

sealed class CharacterUiState {
    data object Loading : CharacterUiState()
    data class Success(val characters: List<CharacterEntity>) : CharacterUiState()
    data class Error(val message: String) : CharacterUiState()
}