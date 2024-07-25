package com.example.rickandmortyapplication.data.repository

import android.util.Log
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
            if (query.isBlank()) {
                val cachedEpisodes = episodeDao.getEpisodesForPage(page)
                if (cachedEpisodes.isNotEmpty()) {return@withContext cachedEpisodes
                }
            }

            val episodes = try {
                val response = api.getAllEpisodes(page)
                if (response.isSuccessful && response.body() != null) {
                    response.body()!!.results.map { episodeResponse ->
                        val airDate = episodeResponse.air_date ?: ""
                        EpisodeEntity(
                            id = episodeResponse.id,
                            name = episodeResponse.name,
                            air_date = airDate,
                            episode = episodeResponse.episode,
                            page = page
                        )}
                } else {
                    emptyList()
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

    fun getEpisodeById(id: Int): Flow<EpisodeEntity> {
        return episodeDao.getEpisodeById(id)
    }
}
