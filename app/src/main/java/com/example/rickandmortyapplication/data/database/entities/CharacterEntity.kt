package com.example.rickandmortyapplication.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.rickandmortyapplication.data.database.Converters
import com.example.rickandmortyapplication.data.model.CharacterLocationDto
import com.example.rickandmortyapplication.data.model.OriginDto

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String?,
    val gender: String,
    @TypeConverters(Converters::class)
    val origin: OriginDto,
    @TypeConverters(Converters::class)
    val location: CharacterLocationDto,
    val image: String,
    val episode: List<String>,
    val url: String,
    val created: String,
    var page: Int? = null
)