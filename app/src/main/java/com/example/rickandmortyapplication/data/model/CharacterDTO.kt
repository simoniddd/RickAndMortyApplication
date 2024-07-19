package com.example.rickandmortyapplication.data.model

    data class CharacterDto(
        val id: Int,
        val name: String,
        val species: String,
        val status: String,
        val origin: String,
        val gender: String,
        val image: String,
        val episodes: List<String>
    )
