package com.example.rickandmortyapplication.ui.episodes

import EpisodeRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapplication.data.database.entities.EpisodeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class EpisodeViewModel(
    application: Application,
    private val repository: EpisodeRepository
) : AndroidViewModel(application) {

    // Храним текущий поисковый запрос
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Получаем все эпизоды
    private val _allEpisodes = repository.getAllEpisodes()
    val allEpisodes: Flow<List<EpisodeEntity>> = _allEpisodes

    // Фильтруем эпизоды на основе поискового запроса
    val filteredEpisodes: Flow<List<EpisodeEntity>> = searchQuery
        .flatMapLatest { query ->
            repository.getFilteredEpisodes(query)
        }

    // Храним текущий идентификатор эпизода
    private val _episodeId = MutableStateFlow<Int?>(null)

    // Получаем данные эпизода по ID
    val episodeDetail: Flow<EpisodeEntity?> = _episodeId.flatMapLatest { id ->
        id?.let { repository.getEpisodeById(it) } ?: flowOf(null)
    }

    init {
        // Инициализируем получение эпизодов, если нужно
        viewModelScope.launch {
            repository.refreshEpisodes(page = 1)  // Обновите страницу, если требуется
        }
    }

    // Метод для установки поискового запроса
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Метод для установки идентификатора эпизода
    fun setEpisodeId(id: Int) {
        _episodeId.value = id
    }

    // Метод для получения эпизода по ID (если нужно)
    fun getEpisodeById(id: Int): Flow<EpisodeEntity?> {
        return repository.getEpisodeById(id)
    }

    // Метод для обновления эпизодов
    fun refreshEpisodes(page: Int) {
        viewModelScope.launch {
            repository.refreshEpisodes(page)
        }
    }
}
