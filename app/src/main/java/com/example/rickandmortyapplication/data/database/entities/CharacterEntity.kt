package com.example.rickandmortyapplication.data.database.entities
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val species: String,
    val status: String,
    val origin: String,
    val gender: String,
    val image: String,
    val episodes: List<String>
)