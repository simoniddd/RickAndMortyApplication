package com.example.rickandmortyapplication.ui.character

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapplication.data.database.entities.CharacterEntity
import com.example.rickandmortyapplication.data.database.entities.EpisodeEntity
import com.example.rickandmortyapplication.data.network.RetrofitInstance.api
import com.example.rickandmortyapplication.data.repository.CharacterRepository
import com.example.rickandmortyapplication.ui.filters.CharacterFilterDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CharacterViewModel(
    application: Application,
    private val repository: CharacterRepository
) : AndroidViewModel(application) {

    private val _characterUiState = MutableStateFlow<CharacterUiState>(CharacterUiState.Loading)
    val characterUiState: StateFlow<CharacterUiState> = _characterUiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val ITEMS_PER_PAGE = 20

    private var currentPage = 1
    private var isLastPage = false

    init {
        loadCharacters()
    }

    private val _nameFilter = MutableStateFlow("")
    private val _statusFilter = MutableStateFlow("")
    private val _speciesFilter = MutableStateFlow("")
    private val _genderFilter = MutableStateFlow("")

    fun applyFilters(filters: CharacterFilterDialogFragment.CharacterFilterData) {
        _nameFilter.value = filters.name
        _statusFilter.value = filters.status
        _speciesFilter.value = filters.species
        _genderFilter.value = filters.gender
        currentPage = 1
        isLastPage = false
        loadCharacters()
    }

    fun loadCharacters() {
        viewModelScope.launch {
            _characterUiState.value = CharacterUiState.Loading
            try {
                repository.getCharacters(
                    page = currentPage,
                    name = _nameFilter.value,
                    status = _statusFilter.value,
                    species = _speciesFilter.value,
                    gender = _genderFilter.value
                ).collect { characters ->
                    if (characters.isNotEmpty()) {
                        _characterUiState.value = CharacterUiState.Success(characters)
                        isLastPage = characters.size < ITEMS_PER_PAGE // ITEMS_PER_PAGE — это количество элементов на одной странице
                        if (!isLastPage) {
                            currentPage++
                        }
                    } else {
                        _characterUiState.value = CharacterUiState.Success(emptyList())
                        isLastPage = true
                    }
                }
            } catch (e: Exception) {
                _characterUiState.value = CharacterUiState.Error("Failed to load characters: ${e.message}")
            }
        }
    }


    fun loadNextPage() {
        if (!isLastPage && _searchQuery.value.isBlank()) {
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
        loadCharacters()
    }
}

sealed class CharacterUiState {
    object Loading : CharacterUiState()
    data class Success(val characters: List<CharacterEntity>) : CharacterUiState()
    data class Error(val message: String) : CharacterUiState()
}
