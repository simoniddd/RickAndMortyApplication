package com.example.rickandmortyapplication.data.model

import com.example.rickandmortyapplication.data.database.entities.EpisodeEntity

data class EpisodeDTO(
    val id: Int,
    val name: String,
    val air_date: String,
    val episode: String,
    val characters: List<String>
)

fun EpisodeDTO.toEpisodeEntity(): EpisodeEntity {
    return EpisodeEntity(id, name, air_date, episode, characters)
}