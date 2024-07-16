package com.example.rickandmortyapplication.data.database.entities
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "episodes")
data class EpisodeEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val airdate: String,
    val url: String
)