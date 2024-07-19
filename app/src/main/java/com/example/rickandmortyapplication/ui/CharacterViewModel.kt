package com.example.myapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.data.repository.CharacterRepository
import com.example.rickandmortyapplication.data.database.entities.CharacterEntity
import com.example.rickandmortyapplication.data.database.entities.EpisodeEntity
import com.example.rickandmortyapplication.data.network.RetrofitInstance.api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CharacterViewModel(
    application: Application,
    private val repository: CharacterRepository
) : AndroidViewModel(application) {

    // Хранение поискового запроса
    private val searchQuery = MutableStateFlow("")

    // открытый поток всех персонажей
    val allCharacters: Flow<List<CharacterEntity>> = repository.getAllCharacters()

    // Фильтрованные результаты поиска
    val filteredCharacters: Flow<List<CharacterEntity>> = searchQuery
        .flatMapLatest { query ->
            allCharacters.map { characters ->
                characters.filter { character ->
                    character.name.contains(query, ignoreCase = true)
                }
            }
        }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun refreshCharacters(page: Int) {
        viewModelScope.launch {
            repository.refreshCharacters(page)
        }
    }

    fun getCharacter(characterId: Int): Flow<CharacterEntity> {
        return repository.getCharacter(characterId)
    }

    suspend fun getEpisodeData(episodeUrl: String): EpisodeEntity {
        return withContext(Dispatchers.IO) {
            api.getEpisode(episodeUrl)
        }
    }
}
