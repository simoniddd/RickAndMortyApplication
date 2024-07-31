package com.example.rickandmortyapplication.data.model.responses

import com.example.rickandmortyapplication.data.model.dto.CharacterDto

data class CharacterResponse(
    val info: Info,
    val results: List<CharacterDto>
)

data class Info(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?
)




