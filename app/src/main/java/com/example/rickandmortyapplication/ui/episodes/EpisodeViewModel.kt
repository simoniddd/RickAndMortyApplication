package com.example.rickandmortyapplication.ui.episodes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapplication.data.database.entities.EpisodeEntity
import com.example.rickandmortyapplication.data.repository.EpisodeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class EpisodeViewModel(
    application: Application,
    private val repository: EpisodeRepository
) : AndroidViewModel(application) {private val _episodeUiState = MutableStateFlow<EpisodeUiState>(EpisodeUiState.Loading)
    val episodeUiState: StateFlow<EpisodeUiState> = _episodeUiState.asStateFlow()

    private var currentPage = 1
    var isLastPage = false
    private var currentQuery = ""

    init {
        loadEpisodes()
    }

    fun loadEpisodes(query: String = "") {
        viewModelScope.launch {
            _episodeUiState.value = EpisodeUiState.Loading
            try {
                val episodes = repository.getEpisodes(currentPage, query)
                _episodeUiState.value = EpisodeUiState.Success(episodes)
                isLastPage = episodes.isEmpty()
                currentPage++
            } catch (e: Exception) {
                _episodeUiState.value = EpisodeUiState.Error("Failed to loadepisodes")
            }
        }
    }

    fun loadNextPage() {
        if (!isLastPage) {
            loadEpisodes(currentQuery)
        }
    }

    fun setSearchQuery(query: String) {
        currentQuery = query
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
