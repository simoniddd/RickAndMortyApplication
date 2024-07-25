package com.example.rickandmortyapplication.data.repository

import com.example.rickandmortyapplication.data.database.CharacterDao
import com.example.rickandmortyapplication.data.database.entities.CharacterEntity
import com.example.rickandmortyapplication.data.model.toEntity
import com.example.rickandmortyapplication.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CharacterRepository(
    private val api: ApiService,
    private val characterDao: CharacterDao
) {

    suspend fun getCharacters(page: Int, query: String = ""): List<CharacterEntity> {
        return withContext(Dispatchers.IO) {
            if (query.isBlank()) {
                // No search query, handle pagination
                val cachedCharacters = characterDao.getCharactersForPage(page)
                if (cachedCharacters.isNotEmpty()) {
                    cachedCharacters
                } else {
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
                }
            } else {
                val filteredCharacters = characterDao.getAllCharacters()
                    .map { characterList ->
                        characterList.filter { character ->
                            character.name.contains(query, ignoreCase = true)
                        }
                    }
                    .first()
                filteredCharacters
            }
        }
    }

    fun getCharacterById(characterId: Int): Flow<CharacterEntity> {
        return characterDao.getCharacterById(characterId)
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
