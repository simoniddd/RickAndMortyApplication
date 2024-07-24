package com.example.rickandmortyapplication.data.database.entities
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "episodes")
data class EpisodeEntity(
    @PrimaryKey val id: Int,
    val name: String,
    @SerializedName("air_date") val airdate: String,
    val episode: String
)