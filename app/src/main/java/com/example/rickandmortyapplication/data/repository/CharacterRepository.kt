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
import kotlinx.coroutines.withContext

class CharacterRepository(
    private val api: ApiService,
    private val characterDao: CharacterDao
) {

    suspend fun getCharacters(
        page: Int,
        name: String = "",
        status: String = "",
        species: String = "",
        gender: String = "",
        searchQuery: String = ""
    ): List<CharacterEntity> {
        return withContext(Dispatchers.IO) {
            val allCachedCharacters = getAllCachedCharacters().first()
            val cachedCharacters = getCharactersForPage(page, allCachedCharacters)
            if (cachedCharacters.isNotEmpty()) {
                filterCharactersInCache(
                    name,
                    status,
                    species,
                    gender,
                    searchQuery,
                    cachedCharacters
                )
            } else {
                val fetchedCharacters = fetchCharactersFromApi(page)
                filterCharactersInCache(
                    name,
                    status,
                    species,
                    gender,
                    searchQuery,
                    fetchedCharacters
                )
            }
        }
    }

    private fun getAllCachedCharacters(): Flow<List<CharacterEntity>> {
        return characterDao.getAllCharacters()
    }

    private fun getCharactersForPage(
        page: Int,
        allCharacters: List<CharacterEntity>
    ): List<CharacterEntity> {
        return allCharacters.filter { it.page == page }
    }

    private suspend fun fetchCharactersFromApi(page: Int): List<CharacterEntity> {
        return try {
            val response = api.getAllCharacters(page)
            if (response.isSuccessful && response.body() != null) {
                val characters = response.body()!!.results.map { it.toCharacterEntity() }
                characters.forEach { it.page = page }
                characterDao.insertCharacters(characters)
                characters
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun filterCharactersInCache(
        name: String,
        status: String,
        species: String,
        gender: String,
        searchQuery: String,
        dbCharacters: List<CharacterEntity>
    ): List<CharacterEntity> {
        return dbCharacters.filter { character ->
            (searchQuery.isBlank() || character.name.contains(searchQuery, ignoreCase = true)) &&
                    (name.isBlank() || character.name.contains(name, ignoreCase = true)) &&
                    (status.isBlank() || character.status == status) &&
                    (species.isBlank() || character.species.contains(species, ignoreCase = true)) &&
                    (gender.isBlank() || character.gender == gender)
        }
    }

    suspend fun getCharacterById(id: Int): CharacterDto {
        return api.getCharacterById(id)
    }

    suspend fun getEpisodeByUrl(url: String): EpisodeDTO {
        return api.getEpisodeByUrl(url)
    }
}


