package com.example.rickandmortyapplication.ui.character

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapplication.data.repository.CharacterRepository
import com.example.rickandmortyapplication.ui.filters.CharacterFilterDialogFragment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class CharacterViewModel(
    application: Application,
    private val repository: CharacterRepository
) : AndroidViewModel(application) {

    private val _characterUiState = MutableStateFlow<CharacterUiState>(CharacterUiState.Loading)
    val characterUiState: StateFlow<CharacterUiState> = _characterUiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val _filters = MutableStateFlow(CharacterFilters())

    private var currentPage = 1
    private var isLastPage = false

    init {
        observeFiltersAndQuery()
    }

    private fun observeFiltersAndQuery() {
        viewModelScope.launch {
            combine(
                _filters,
                _searchQuery
            ) { filters, query ->
                filters to query

            }.debounce(300)
                .collect { (filters, query) ->
                    val (name, status, species, gender) = filters
                    currentPage = 1
                    isLastPage = false
                    loadCharacters(filters, query)
                }
        }
    }

    fun loadCharacters(filters: CharacterFilters, query: String) {
        viewModelScope.launch {
            _characterUiState.value = CharacterUiState.Loading
            try {
                val characters = repository.getCharacters(
                    page = currentPage,
                    name = filters.name,
                    status = filters.status,
                    species = filters.species,
                    gender = filters.gender,
                    searchQuery = query
                )
                _characterUiState.value = CharacterUiState.Success(characters)
                isLastPage = characters.isEmpty() || characters.size < 20
                if (!isLastPage) {
                    currentPage++
                }
            } catch (e: Exception) {
                _characterUiState.value =
                    CharacterUiState.Error("Failed to load characters: ${e.message}")
            }
        }
    }


    fun loadNextPage() {
        if (!isLastPage && _searchQuery.value.isBlank()) {
            loadCharacters(_filters.value, "")
        }
    }

    fun applyFilters(filters: CharacterFilterDialogFragment.CharacterFilterData) {
        _filters.value = CharacterFilters(
            name = filters.name,
            status = if (filters.status == "All") "" else filters.status,
            species = filters.species,
            gender = if (filters.gender == "All") "" else filters.gender
        )
    }

    fun clearFilters() {
        _filters.value = CharacterFilters()
        _searchQuery.value = ""
    }

    private fun resetPagination() {
        currentPage = 1
        isLastPage = false
        loadCharacters(_filters.value, "")
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        resetPagination()
    }
}

data class CharacterFilters(
    val name: String = "",
    val status: String = "",
    val species: String = "",
    val gender: String = ""
)
