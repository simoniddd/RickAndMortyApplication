package com.example.rickandmortyapplication.data.repository

import com.example.rickandmortyapplication.data.database.EpisodeDao
import com.example.rickandmortyapplication.data.database.entities.CharacterEntity
import com.example.rickandmortyapplication.data.database.entities.EpisodeEntity
import com.example.rickandmortyapplication.data.model.CharacterDto
import com.example.rickandmortyapplication.data.model.EpisodeDTO
import com.example.rickandmortyapplication.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
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
            // Получение всех эпизодов из базы данных
            val dbEpisodes = episodeDao.getAllEpisodes()

            // Если нет фильтров, проверяем наличие кэшированных данных
            if (name.isBlank() && episode.isBlank() && searchQuery.isBlank()) {
                val cachedEpisodes = episodeDao.getEpisodesForPage(page)
                if (cachedEpisodes.isNotEmpty()) {
                    return@withContext cachedEpisodes
                } else {
                    try {
                        // Запрос эпизодов из сети
                        val response = api.getAllEpisodes(page)
                        if (response.isSuccessful && response.body() != null) {
                            val episodes = response.body()!!.results.map { episodeResponse ->
                                EpisodeEntity(
                                    episodeResponse.id,
                                    episodeResponse.name,
                                    episodeResponse.air_date,
                                    episodeResponse.episode,
                                    episodeResponse.characters
                                )
                            }
                            // Сохранение эпизодов в базу данных
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
                // Фильтрация кэшированных данных
                return@withContext dbEpisodes
                    .map { episodeList ->
                        episodeList.filter { episodeEntity ->
                            (searchQuery.isBlank() || episodeEntity.name.contains(searchQuery, ignoreCase = true)) &&
                            (name.isBlank() || episodeEntity.name.contains(name, ignoreCase = true)) &&
                                    (episode.isBlank() || episodeEntity.episode.contains(episode, ignoreCase = true))
                        }
                    }
                    .first()
            }
        }
    }

    suspend fun getEpisodeById(id: Int): EpisodeDTO {
        return api.getEpisode(id)
    }

    suspend fun getCharacterByUrl(url: String): CharacterDto {
        // Получите информацию о персонаже по URL
        return api.getCharacterByUrl(url)
    }
}
