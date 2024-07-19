package com.example.myapp.ui

import EpisodeRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapplication.data.database.entities.EpisodeEntity
import com.example.rickandmortyapplication.data.network.RetrofitInstance.api
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class EpisodeViewModel(application: Application) : AndroidViewModel(application) {
    private val EpisodeDao = AppDatabase.getDatabase(application).episodeDao()
    private val repository = EpisodeRepository(api, EpisodeDao)

    val allEpisodes: Flow<List<EpisodeEntity>> = repository.getAllEpisodes()

    fun refreshEpisodes(page: Int) {
        viewModelScope.launch {
            repository.refreshEpisodes(page)
        }
    }
}