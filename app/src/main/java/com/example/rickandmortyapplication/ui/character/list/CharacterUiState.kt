package com.example.rickandmortyapplication.ui.character.list

import com.example.rickandmortyapplication.data.database.entities.CharacterEntity

sealed class CharacterUiState {
    object Loading : CharacterUiState()
    data class Success(val characters: List<CharacterEntity>) : CharacterUiState()
    data class Error(val message: String) : CharacterUiState()
}