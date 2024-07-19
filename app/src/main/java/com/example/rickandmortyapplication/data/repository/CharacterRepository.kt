package com.example.myapp.data.repository

import CharacterDao
import com.example.rickandmortyapplication.data.database.entities.CharacterEntity
import com.example.rickandmortyapplication.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CharacterRepository(
    private val api: ApiService,
    private val characterDao: CharacterDao
) {
    suspend fun refreshCharacters(page: Int) {
        withContext(Dispatchers.IO) {
            val response = api.getAllCharacters(page)
            val characters = response.results.map {
                CharacterEntity(it.id, it.name, it.species, it.status, it.origin, it.gender, it.image, it.episodes)
            }
            characterDao.insertCharacters(characters)
        }
    }

    fun getAllCharacters(): Flow<List<CharacterEntity>> {
        return characterDao.getAllCharacters()
    }

    fun getCharacter(characterId: Int): Flow<CharacterEntity> {
        return characterDao.getCharacterById(characterId)
    }
}
