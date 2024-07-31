package com.example.rickandmortyapplication.ui.character

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapplication.data.repository.CharacterRepository
import com.example.rickandmortyapplication.ui.filters.CharacterFilterDialogFragment
import com.example.rickandmortyapplication.ui.locations.Quad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CharacterViewModel(
    application: Application,
    private val repository: CharacterRepository
) : AndroidViewModel(application) {

    private val _characterUiState = MutableStateFlow<CharacterUiState>(CharacterUiState.Loading)
    val characterUiState: StateFlow<CharacterUiState> = _characterUiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _nameFilter = MutableStateFlow("")
    private val _statusFilter = MutableStateFlow("")
    private val _speciesFilter = MutableStateFlow("")
    private val _genderFilter = MutableStateFlow("")

    private var currentPage = 1
    private var isLastPage = false

    init {
        observeFiltersAndQuery()
    }

    private fun observeFiltersAndQuery() {
        viewModelScope.launch {
            combine(
                _nameFilter,
                _statusFilter,
                _speciesFilter,
                _genderFilter,
                _searchQuery
            ) { name, status, species, gender, query ->
                Quad(name, status, species, gender) to query
            }.collect { (filters, query) ->
                val (name, status, species, gender) = filters
                currentPage = 1
                isLastPage = false
                loadCharacters(name, status, species, gender, query)
            }
        }
    }

    fun loadCharacters(
        name: String = _nameFilter.value,
        status: String = _statusFilter.value,
        species: String = _speciesFilter.value,
        gender: String = _genderFilter.value,
        query: String = _searchQuery.value
    ) {
        viewModelScope.launch {
            _characterUiState.value = CharacterUiState.Loading
            try {
                val characters = repository.getCharacters(
                    page = currentPage,
                    name = name,
                    status = status,
                    species = species,
                    gender = gender,
                    searchQuery = query
                )
                _characterUiState.value = CharacterUiState.Success(characters)
                isLastPage = characters.isEmpty()
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


    fun applyFilters(filters: CharacterFilterDialogFragment.CharacterFilterData) {
        _nameFilter.value = filters.name
        _statusFilter.value = if (filters.status == "All") "" else filters.status
        _speciesFilter.value = filters.species
        _genderFilter.value = if (filters.gender == "All") "" else filters.gender
        resetPagination()
        loadCharacters()
    }

    fun clearFilters() {
        _nameFilter.value = ""
        _statusFilter.value = ""
        _speciesFilter.value = ""
        _genderFilter.value = ""
        _searchQuery.value = ""
        resetPagination()
        loadCharacters()
    }

    private fun resetPagination() {
        currentPage = 1
        isLastPage = false
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        resetPagination()
        loadCharacters()
    }
}
