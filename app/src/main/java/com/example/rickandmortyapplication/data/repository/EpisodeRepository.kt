package com.example.rickandmortyapplication.data.repository

import com.example.rickandmortyapplication.data.database.EpisodeDao
import com.example.rickandmortyapplication.data.database.entities.EpisodeEntity
import com.example.rickandmortyapplication.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class EpisodeRepository(
    private val api: ApiService,
    private val episodeDao: EpisodeDao
) {
    suspend fun getEpisodes(
        page: Int,
        name: String = "",
        episode: String = ""
    ): List<EpisodeEntity> {
        return withContext(Dispatchers.IO) {
            val dbEpisodes = episodeDao.getAllEpisodes()
            if (name.isBlank() && episode.isBlank()) {
                val cachedEpisodes = episodeDao.getEpisodesForPage(page)
                if (cachedEpisodes.isNotEmpty()) {
                    return@withContext cachedEpisodes
                } else {
                    try {
                        val response = api.getAllEpisodes(page)
                        if (response.isSuccessful && response.body() != null) {
                            val episodes = response.body()!!.results.map { episodeResponse ->
                                val airDate = episodeResponse.air_date ?: ""
                                EpisodeEntity(
                                    id = episodeResponse.id,
                                    name = episodeResponse.name,
                                    air_date = airDate,
                                    episode = episodeResponse.episode,
                                    page = page
                                )
                            }
                            episodeDao.insertEpisodes(episodes)
                            return@withContext episodes
                        } else {
                            return@withContext emptyList()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        return@withContext emptyList()
                    }
                }
            } else {
                return@withContext dbEpisodes
                    .map { episodeList ->
                        episodeList.filter { episodeEntity ->
                            (name.isBlank() || episodeEntity.name.contains(name, ignoreCase = true)) &&
                                    (episode.isBlank() || episodeEntity.episode.contains(episode, ignoreCase = true))
                        }
                    }
                    .first()
            }
        }
    }

    fun getEpisodeById(id: Int): Flow<EpisodeEntity> {
        return episodeDao.getEpisodeById(id)
    }
}
