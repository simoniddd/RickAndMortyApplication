package com.example.rickandmortyapplication.ui.episodes

import com.example.rickandmortyapplication.data.database.entities.CharacterEntity
import com.example.rickandmortyapplication.data.model.EpisodeDTO

sealed class EpisodeDetailsUiState {
    object Loading : EpisodeDetailsUiState()
    data class Success(
        val episode: EpisodeDTO,
        val characters: List<CharacterEntity>
    ) : EpisodeDetailsUiState()

    data class Error(val message: String) : EpisodeDetailsUiState()
}