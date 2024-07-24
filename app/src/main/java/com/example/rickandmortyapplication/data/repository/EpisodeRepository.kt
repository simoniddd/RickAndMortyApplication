package com.example.rickandmortyapplication.data.repository

import com.example.rickandmortyapplication.data.database.EpisodeDao
import com.example.rickandmortyapplication.data.database.entities.EpisodeEntity
import com.example.rickandmortyapplication.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class EpisodeRepository(
    private val api: ApiService,
    private val episodeDao: EpisodeDao
) {
    suspend fun getEpisodes(page: Int, query: String = ""): List<EpisodeEntity> {
        return withContext(Dispatchers.IO) {
            val cachedEpisodes = episodeDao.getEpisodesForPage(page)
            if (cachedEpisodes.isNotEmpty() && query.isBlank()) {
                cachedEpisodes} else {
                val episodes = try {
                    val response = api.getAllEpisodes(page)
                    response.results.map { episodeResponse ->
                        val airDate = episodeResponse.airdate ?: ""
                        EpisodeEntity(
                            id = episodeResponse.id,
                            name = episodeResponse.name,
                            airdate = airDate,
                            episode = episodeResponse.episode,
                            page = page // Add page number
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    emptyList()
                }
                val filteredEpisodes = if (query.isNotBlank()) {
                    episodes.filter { it.name.contains(query, ignoreCase = true) }
                } else {
                    episodes
                }
                episodeDao.insertEpisodes(filteredEpisodes)
                filteredEpisodes
            }
        }
    }

    fun getEpisodeById(id: Int): Flow<EpisodeEntity> {
        return episodeDao.getEpisodeById(id)
    }
}
