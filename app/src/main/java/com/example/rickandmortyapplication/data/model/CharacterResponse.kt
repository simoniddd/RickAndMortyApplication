package com.example.myapp.data.model

import com.example.rickandmortyapplication.data.model.CharacterDto

data class CharacterResponse(
    val results: List<CharacterDto>
)