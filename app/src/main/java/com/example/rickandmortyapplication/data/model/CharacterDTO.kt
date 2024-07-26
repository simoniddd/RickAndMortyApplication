package com.example.rickandmortyapplication.data.model

import com.example.rickandmortyapplication.data.database.entities.CharacterEntity

data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: OriginDto,
    val location: CharacterLocationDto,
    val image: String,
    val episode: List<String>,
    val url: String,
    val created: String
)

data class OriginDto(
    val name: String,
    val url: String
)

data class CharacterLocationDto(
    val name: String,
    val url: String
)

fun CharacterDto.toEntity(): CharacterEntity {
    return CharacterEntity(
        id = this.id,
        name = this.name,
        status = this.status,
        species = this.species,
        gender = this.gender,
        image = this.image,
        created = this.created,
        origin = this.origin,
        location = this.location,
        episode = this.episode,
        type = this.type,
        url = this.url
    )
}
