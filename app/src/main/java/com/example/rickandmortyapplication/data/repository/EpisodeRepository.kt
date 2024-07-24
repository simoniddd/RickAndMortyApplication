package com.example.rickandmortyapplication.data.repository

import com.example.rickandmortyapplication.data.database.EpisodeDao
import com.example.rickandmortyapplication.data.database.entities.EpisodeEntity
import com.example.rickandmortyapplication.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class EpisodeRepository(private val api: ApiService,
                        private val episodeDao: EpisodeDao) {

    // Функция для получения эпизодов с API и сохранения их в базе данных
    suspend fun refreshEpisodes(page: Int) {
        withContext(Dispatchers.IO) {
            val response = api.getAllEpisodes(page)
            val episodes = response.results.map {
                episodeResponse ->
            val airDate = episodeResponse.airdate ?: ""
            EpisodeEntity(
                id = episodeResponse.id,
                name = episodeResponse.name,
                airdate = airDate,
                episode = episodeResponse.episode,
            )
        }
            episodeDao.insertEpisodes(episodes)
        }
    }

    fun getAllEpisodes(): Flow<List<EpisodeEntity>> {
            return episodeDao.getAllEpisodes()
        }

    fun getEpisodeById(id: Int): Flow<EpisodeEntity> {
        return episodeDao.getEpisodeById(id)
    }

    // Метод для получения отфильтрованных эпизодов
    fun getFilteredEpisodes(query: String): Flow<List<EpisodeEntity>> {
        return episodeDao.getAllEpisodes()
            .map { episodes ->
                episodes.filter { it.name.contains(query, ignoreCase = true) }
            }
    }
}
