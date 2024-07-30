package com.example.rickandmortyapplication.data.repository

import com.example.rickandmortyapplication.data.database.CharacterDao
import com.example.rickandmortyapplication.data.database.entities.CharacterEntity
import com.example.rickandmortyapplication.data.model.CharacterDto
import com.example.rickandmortyapplication.data.model.EpisodeDTO
import com.example.rickandmortyapplication.data.model.toCharacterEntity
import com.example.rickandmortyapplication.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CharacterRepository(
    private val api: ApiService,
    private val characterDao: CharacterDao
) {

    fun getCharacters(
        page: Int,
        name: String = "",
        status: String = "",
        species: String = "",
        gender: String = "",
        searchQuery: String = ""
    ): Flow<List<CharacterEntity>> = flow {
        val response = api.getAllCharacters(page)
        if (response.isSuccessful) {
            response.body()?.let { characterResponse ->
                val characters = characterResponse.results.map { it.toCharacterEntity() }
                characters.forEach { it.page = page }
                characterDao.insertCharacters(characters)
                val updatedCharacters = characterDao.getAllCharacters().first()
                emit(updatedCharacters)
            } ?: emit(emptyList())
        } else {
            emit(emptyList())
        }
    }.map { characters ->
        characters.filter { character ->
            (searchQuery.isBlank() || character.name.contains(searchQuery, ignoreCase = true)) &&
                    (name.isBlank() || character.name.contains(name, ignoreCase = true)) &&
                    (status.isBlank() || character.status == status) &&
                    (species.isBlank() || character.species.contains(species, ignoreCase = true)) &&
                    (gender.isBlank() || character.gender == gender)
        }
    }.flowOn(Dispatchers.IO)


    suspend fun getCharacterById(id: Int): CharacterDto {
        return api.getCharacterById(id)
    }

    suspend fun refreshCharacters(page: Int): List<CharacterEntity> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getAllCharacters(page)
                if (response.isSuccessful) {
                    response.body()?.let { characterResponse ->
                        val characters = characterResponse.results.map { it.toCharacterEntity() }
                        characters.forEach { it.page = page }
                        characterDao.insertCharacters(characters)
                        characters
                    } ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    suspend fun getEpisodeByUrl(url: String): EpisodeDTO {
        return api.getEpisodeByUrl(url)
    }
}

