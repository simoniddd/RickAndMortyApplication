package com.example.rickandmortyapplication.data.repository

import com.example.rickandmortyapplication.data.database.CharacterDao
import com.example.rickandmortyapplication.data.database.entities.CharacterEntity
import com.example.rickandmortyapplication.data.model.toEntity
import com.example.rickandmortyapplication.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
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
        gender: String = ""
    ): Flow<List<CharacterEntity>> = flow {
        // Fetch characters from API
        val response = api.getAllCharacters(page)
        if (response.isSuccessful) {
            response.body()?.let { characterResponse ->
                val characters = characterResponse.results.map { it.toEntity() }
                characters.forEach { it.page = page }
                characterDao.insertCharacters(characters)

                // Emit updated characters from cache
                val updatedCharacters = characterDao.getAllCharacters().first()
                emit(updatedCharacters)
            } ?: emit(emptyList())
        } else {
            emit(emptyList())
        }
    }.map { characters ->
        // Filter the characters based on the provided criteria
        characters.filter { character ->
            (name.isBlank() || character.name.contains(name, ignoreCase = true)) &&
                    (status.isBlank() || character.status == status) &&
                    (species.isBlank() || character.species.contains(species, ignoreCase = true)) &&
                    (gender.isBlank() || character.gender == gender)
        }
    }.flowOn(Dispatchers.IO)


    fun getCharacterById(characterId: Int): Flow<CharacterEntity> {
        return characterDao.getCharacterById(characterId).flowOn(Dispatchers.IO)
    }

    suspend fun refreshCharacters(page: Int): List<CharacterEntity> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getAllCharacters(page)
                if (response.isSuccessful) {
                    response.body()?.let { characterResponse ->
                        val characters = characterResponse.results.map { it.toEntity() }
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

    fun deleteAll() {
        characterDao.deleteAll()
    }
}

