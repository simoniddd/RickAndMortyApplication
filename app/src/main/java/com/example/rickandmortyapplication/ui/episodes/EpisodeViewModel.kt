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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class EpisodeViewModel(
    application: Application,
    private val repository: EpisodeRepository
) : AndroidViewModel(application) {
    private val _episodeUiState = MutableStateFlow<EpisodeUiState>(EpisodeUiState.Loading)
    val episodeUiState: StateFlow<EpisodeUiState> = _episodeUiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var currentPage = 1
    private var isLastPage = false

    init {
        loadEpisodes()
    }

    private val _nameFilter = MutableStateFlow("")
    private val _episodeFilter = MutableStateFlow("")

    fun applyFilters(filters: EpisodeFilterDialogFragment.EpisodeFilterData) {
        _nameFilter.value = filters.name
        _episodeFilter.value = filters.episode
        currentPage = 1
        isLastPage = false
        loadEpisodes()
    }

    fun loadEpisodes(query: String = "") {
        viewModelScope.launch {
            _episodeUiState.value = EpisodeUiState.Loading
            try {
                val episodes = repository.getEpisodes(
                    page = currentPage,
                    name = _nameFilter.value,
                    episode = _episodeFilter.value
                )
                _episodeUiState.value = EpisodeUiState.Success(episodes)
                isLastPage = episodes.isEmpty()
                if (!isLastPage) {
                    currentPage++
                }
            } catch (e: Exception) {
                _episodeUiState.value = EpisodeUiState.Error("Failed to load episodes")
            }
        }
    }

    fun loadNextPage() {
        if (!isLastPage && searchQuery.value.isBlank()) { // Only load next page if not searching
            loadEpisodes()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        currentPage = 1
        isLastPage = false
        loadEpisodes(query)
    }

    fun getEpisodeById(episodeId: Int): Flow<EpisodeEntity> {
        return repository.getEpisodeById(episodeId)
    }
}

sealed class EpisodeUiState {
    object Loading : EpisodeUiState()
    data class Success(val episodes: List<EpisodeEntity>) : EpisodeUiState()
    data class Error(val message: String) : EpisodeUiState()
}
