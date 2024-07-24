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
    private var repository: CharacterRepository
) : AndroidViewModel(application) {

    private val _characterUiState = MutableStateFlow<CharacterUiState>(CharacterUiState.Loading)
    val characterUiState: StateFlow<CharacterUiState> = _characterUiState.asStateFlow()

    // For pagination
    private var currentPage = 1
    private var isLastPage = false

    init {
        loadCharacters()
        Log.d("Load Characters", "characters loaded from db")
    }

    // Хранение поискового запроса
    private val _searchQuery = MutableStateFlow("")
    private val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Фильтрованные результаты поиска (You might need to adjust this based on your pagination logic)
    val filteredCharacters: Flow<List<CharacterEntity>> = searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flow { emit(repository.getCharacters(currentPage)) }
                        as Flow<List<CharacterEntity>> // Explicit cast
            } else {
                repository.getFilteredCharacters(query)
            }
        }

    fun loadCharacters() {
        viewModelScope.launch {
            _characterUiState.value = CharacterUiState.Loading
            try {
                var characters = repository.getCharacters(currentPage)
                if (characters.isEmpty()) {
                    // If database is empty, refresh from network
                    characters = repository.refreshCharacters(currentPage)
                }
                _characterUiState.value = CharacterUiState.Success(characters)
                isLastPage = characters.isEmpty()
                currentPage++
            } catch (e: Exception) {
                _characterUiState.value = CharacterUiState.Error("Failed to load characters")
            }
        }
    }

    fun loadNextPage() {
        if (!isLastPage) {
            loadCharacters()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        currentPage = 1
        isLastPage = false
        loadCharacters()
    }

    fun getCharacter(characterId: Int): Flow<CharacterEntity> {
        return repository.getCharacterById(characterId)
    }

    suspend fun getEpisodeData(episodeUrl: String): EpisodeEntity {
        return withContext(Dispatchers.IO) {
            api.getEpisode(episodeUrl)
        }
    }
}

sealed class CharacterUiState {
    data object Loading : CharacterUiState()
    data class Success(var characters: List<CharacterEntity>) : CharacterUiState()
    data class Error(val message: String) : CharacterUiState()
}