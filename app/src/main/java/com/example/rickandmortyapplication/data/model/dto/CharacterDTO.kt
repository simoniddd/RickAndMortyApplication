package com.example.rickandmortyapplication.data.model.dto

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
) {
    fun getLocationId(): Int {
        return url.substringAfterLast("/").toInt()
    }
}

data class CharacterLocationDto(
    val name: String,
    val url: String
)

fun CharacterDto.toCharacterEntity(): CharacterEntity {
    return CharacterEntity(
        id = this.id,
        name = this.name,
        status = this.status,
        species = this.species,
        type = this.type,
        gender = this.gender,
        origin = this.origin,
        location = this.location,
        image = this.image,
        episode = this.episode,
        url = this.url,
        created = this.created
    )
}

fun CharacterEntity.toCharacterDto(): CharacterDto {
    return CharacterDto(
        id = this.id,
        name = this.name,
        status = this.status,
        species = this.species,
        type = this.type.toString(),
        gender = this.gender,
        origin = this.origin,
        location = this.location,
        image = this.image,
        episode = this.episode,
        url = this.url,
        created = this.created
    )
}