package com.example.rickandmortyapplication.data.repository

import com.example.rickandmortyapplication.data.database.dao.EpisodeDao
import com.example.rickandmortyapplication.data.database.entities.EpisodeEntity
import com.example.rickandmortyapplication.data.model.dto.CharacterDto
import com.example.rickandmortyapplication.data.model.dto.EpisodeDTO
import com.example.rickandmortyapplication.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class EpisodeRepository(
    private val api: ApiService,
    private val episodeDao: EpisodeDao
) {

    suspend fun getEpisodes(
        page: Int,
        name: String = "",
        episode: String = "",
        searchQuery: String = ""
    ): List<EpisodeEntity> {
        return withContext(Dispatchers.IO) {
            val dbEpisodes = getAllCachedEpisodes().first()
            if (name.isBlank() && episode.isBlank() && searchQuery.isBlank()) {
                val cachedEpisodes = getEpisodesForPage(page, dbEpisodes)
                if (cachedEpisodes.isNotEmpty()) {
                    return@withContext cachedEpisodes
                } else {
                    val fetchedEpisodes = fetchEpisodesFromApi(page)
                    return@withContext fetchedEpisodes
                }
            } else {
                filterEpisodesInCache(name, episode, searchQuery, dbEpisodes)
            }
        }
    }

    private fun getAllCachedEpisodes(): Flow<List<EpisodeEntity>> {
        return episodeDao.getAllEpisodes()
    }

    private fun getEpisodesForPage(
        page: Int,
        dbEpisodes: List<EpisodeEntity>
    ): List<EpisodeEntity> {
        return dbEpisodes.filter { it.page == page }
    }

    private suspend fun fetchEpisodesFromApi(page: Int): List<EpisodeEntity> {
        return try {
            val response = api.getAllEpisodes(page)
            if (response.isSuccessful && response.body() != null) {
                val episodes = response.body()!!.results.map { episodeResponse ->
                    EpisodeEntity(
                        episodeResponse.id,
                        episodeResponse.name,
                        episodeResponse.air_date,
                        episodeResponse.episode,
                        episodeResponse.characters,
                        page = page
                    )
                }
                episodeDao.insertEpisodes(episodes)
                episodes
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun filterEpisodesInCache(
        name: String,
        episode: String,
        searchQuery: String,
        dbEpisodes: List<EpisodeEntity>
    ): List<EpisodeEntity> {
        return dbEpisodes.filter { episodeEntity ->
            (searchQuery.isBlank() || episodeEntity.name.contains(
                searchQuery,
                ignoreCase = true
            )) &&
                    (name.isBlank() || episodeEntity.name.contains(name, ignoreCase = true)) &&
                    (episode.isBlank() || episodeEntity.episode.contains(
                        episode,
                        ignoreCase = true
                    ))
        }
    }

    suspend fun getEpisodeById(id: Int): EpisodeDTO {
        return api.getEpisode(id)
    }

    suspend fun getCharacterByUrl(url: String): CharacterDto {
        return api.getCharacterByUrl(url)
    }
}

