package com.example.rickandmortyapplication.ui.locations.listLocations

import LocationRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapplication.data.database.entities.LocationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class LocationsViewModel(
    application: Application,
    private val repository: LocationRepository
) : AndroidViewModel(application) {

    // Храним текущий поисковый запрос
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Получаем все локации
    private val _allLocations = repository.getAllLocations()
    val allLocations: Flow<List<LocationEntity>> = _allLocations

    // Фильтруем локации на основе поискового запроса
    val filteredLocations: Flow<List<LocationEntity>> = searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                _allLocations
            } else {
                repository.getFilteredLocations(query)
            }
        }

    init {
        // Инициализируем получение локаций, если нужно
        viewModelScope.launch {
            repository.refreshLocations(page = 1)  // Обновите страницу, если требуется
        }
    }

    // Метод для установки поискового запроса
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Метод для обновления локаций
    fun refreshLocations(page: Int) {
        viewModelScope.launch {
            repository.refreshLocations(page)
        }
    }

    // Метод для получения локации по ID
    fun getLocationById(id: Int): Flow<LocationEntity> {
        return repository.getLocationById(id)
    }
}
