package com.example.rickandmortyapplication.ui.character

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapplication.data.repository.CharacterRepository
import com.example.rickandmortyapplication.ui.filters.CharacterFilterDialogFragment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CharacterViewModel(
    application: Application,
    private val repository: CharacterRepository
) : AndroidViewModel(application) {

    private val _characterUiState = MutableStateFlow<CharacterUiState>(CharacterUiState.Loading)
    val characterUiState: StateFlow<CharacterUiState> = _characterUiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

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

    fun clearFilters() {
        _nameFilter.value = ""
        _statusFilter.value = ""
        _speciesFilter.value = ""
        _genderFilter.value = ""
        _searchQuery.value = ""
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
                    gender = _genderFilter.value,
                    searchQuery = _searchQuery.value
                ).collect { characters ->
                    if (characters.isNotEmpty()) {
                        _characterUiState.value = CharacterUiState.Success(characters)
                        isLastPage = characters.size < ITEMS_PER_PAGE
                        if (!isLastPage) {
                            currentPage++
                        }
                    } else {
                        _characterUiState.value = CharacterUiState.Success(emptyList())
                        isLastPage = true
                    }
                }
            } catch (e: Exception) {
                _characterUiState.value =
                    CharacterUiState.Error("Failed to load characters: ${e.message}")
            }
        }
    }


    fun loadNextPage() {
        if (!isLastPage && _searchQuery.value.isBlank()) {
            currentPage++
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
