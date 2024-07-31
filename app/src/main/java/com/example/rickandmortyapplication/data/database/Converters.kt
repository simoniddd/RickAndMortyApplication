package com.example.rickandmortyapplication.data.database

import androidx.room.TypeConverter
import com.example.rickandmortyapplication.data.model.dto.CharacterLocationDto
import com.example.rickandmortyapplication.data.model.dto.OriginDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(episodes: List<String>?): String? {
        return Gson().toJson(episodes)
    }


    @TypeConverter
    fun fromOriginDto(value: OriginDto): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toOriginDto(value: String): OriginDto {
        return Gson().fromJson(value, OriginDto::class.java)
    }

    @TypeConverter
    fun fromCharacterLocationDto(value: CharacterLocationDto): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toCharacterLocationDto(value: String): CharacterLocationDto {
        return Gson().fromJson(value, CharacterLocationDto::class.java)
    }
}