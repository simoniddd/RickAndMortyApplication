package com.example.rickandmortyapplication.data.model

import com.example.rickandmortyapplication.data.database.entities.CharacterEntity

data class CharacterDto(
        val id: Int,
        val name: String,
        val species: String,
        val status: String,
        val gender: String,
        val image: String,
    )

fun CharacterDto.toEntity(): CharacterEntity {
    return CharacterEntity(
        id = this.id,
        name = this.name,
        status = this.status,
        species = this.species,
        gender = this.gender,
        image = this.image,
    )
}
