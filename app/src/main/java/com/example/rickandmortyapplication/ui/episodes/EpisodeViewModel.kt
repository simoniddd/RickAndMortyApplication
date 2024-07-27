package com.example.rickandmortyapplication.ui.episodes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapplication.data.database.entities.EpisodeEntity
import com.example.rickandmortyapplication.data.repository.EpisodeRepository
import com.example.rickandmortyapplication.ui.filters.EpisodeFilterDialogFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class EpisodeViewModel(
    application: Application,
    private val repository: EpisodeRepository
) : AndroidViewModel(application) {

    private val _episodeUiState = MutableStateFlow<EpisodeUiState>(EpisodeUiState.Loading)
    val episodeUiState: StateFlow<EpisodeUiState> = _episodeUiState

    private val _searchQuery = MutableStateFlow("")
    private val _nameFilter = MutableStateFlow("")
    private val _episodeFilter = MutableStateFlow("")

    val searchQuery: StateFlow<String> = _searchQuery
    val nameFilter: StateFlow<String> = _nameFilter
    val episodeFilter: StateFlow<String> = _episodeFilter

    private var currentPage = 1
    private var isLastPage = false

    init {
        observeFiltersAndQuery()
    }

    private fun observeFiltersAndQuery() {
        viewModelScope.launch {
            combine(
                _nameFilter,
                _episodeFilter
            ) { name, episode ->
                Pair(name, episode)
            }.collect { (name, episode) ->
                currentPage = 1
                isLastPage = false
                loadEpisodes(name, episode)
            }
        }
    }

    fun setFilters(name: String, episode: String) {
        _nameFilter.value = name
        _episodeFilter.value = episode
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun loadEpisodes(name: String = _nameFilter.value, episode: String = _episodeFilter.value) {
        viewModelScope.launch {
            _episodeUiState.value = EpisodeUiState.Loading
            try {
                val episodes = repository.getEpisodes(
                    page = currentPage,
                    name = name,
                    episode = episode
                )
                _episodeUiState.value = EpisodeUiState.Success(episodes)
                isLastPage = episodes.isEmpty()
                if (!isLastPage) {
                    currentPage++
                }
            } catch (e: Exception) {
                _episodeUiState.value = EpisodeUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun loadNextPage() {
        if (!isLastPage && searchQuery.value.isBlank()) { // Only load next page if not searching
            currentPage++
            loadEpisodes()
        }
    }
}
