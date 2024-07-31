package com.example.rickandmortyapplication.ui.episodes.list

import com.example.rickandmortyapplication.data.database.entities.EpisodeEntity

sealed class EpisodeUiState {
    object Loading : EpisodeUiState()
    data class Success(val episodes: List<EpisodeEntity>) : EpisodeUiState()
    data class Error(val message: String) : EpisodeUiState()
}