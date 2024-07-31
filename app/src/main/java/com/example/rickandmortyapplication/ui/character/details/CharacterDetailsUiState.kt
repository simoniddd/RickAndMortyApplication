package com.example.rickandmortyapplication.ui.character.details

import com.example.rickandmortyapplication.data.database.entities.EpisodeEntity
import com.example.rickandmortyapplication.data.model.dto.CharacterDto

sealed class CharacterDetailsUiState {
    object Loading : CharacterDetailsUiState()
    data class Success(
        val character: CharacterDto,
        val episodes: List<EpisodeEntity>
    ) : CharacterDetailsUiState()

    data class Error(val message: String) : CharacterDetailsUiState()
}