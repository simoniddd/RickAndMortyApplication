package com.example.rickandmortyapplication.data.repository

import com.example.rickandmortyapplication.data.database.CharacterDao
import com.example.rickandmortyapplication.data.database.entities.CharacterEntity
import com.example.rickandmortyapplication.data.model.toEntity
import com.example.rickandmortyapplication.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CharacterRepository(
    private val api: ApiService,
    private val characterDao: CharacterDao
) {

    suspend fun getCharacters(page: Int): List<CharacterEntity> {
        return withContext(Dispatchers.IO) {
            val cachedCharacters = characterDao.getCharactersForPage(page)
            (if (cachedCharacters.isNotEmpty()) {
                cachedCharacters // Return cached characters if available
            } else {
                // Fetch from API and cache if not available
                val response = api.getAllCharacters(page)
                if (response.isSuccessful) {
                    response.body()?.let { characterResponse ->
                        val characters = characterResponse.results.map { it.toEntity() }
                        characters.forEach { it.page = page } // Associate page number
                        characterDao.insertCharacters(characters)
                        characters
                    } ?: emptyList()
                } else {
                    emptyList()
                }
            })
        }
    }

    fun getFilteredCharacters(query: String): Flow<List<CharacterEntity>> {
        return characterDao.getCharactersByName(query)
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
                        characters.forEach { it.page = page } // Associate page number
                        characterDao.insertCharacters(characters)
                        characters // Return the fetched and inserted characters
                    } ?: emptyList() // Return empty list if response bodyis null
                } else {
                    emptyList() // Return empty list if API call fails
                }
            } catch (e: Exception) {
                // Handle exception (e.g., log, show error message)
                e.printStackTrace()
                emptyList() // Return an empty list in case of an error
            }
        }
    }

    fun deleteAll() {
        characterDao.deleteAll()
    }
}
