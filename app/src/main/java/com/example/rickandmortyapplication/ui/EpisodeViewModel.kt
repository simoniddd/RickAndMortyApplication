package com.example.myapp.ui.episodes

import EpisodeRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapplication.data.database.entities.EpisodeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class EpisodeViewModel(
    application: Application,
    private val repository: EpisodeRepository
) : AndroidViewModel(application) {

    val allEpisodes: Flow<List<EpisodeEntity>> = repository.getAllEpisodes()

    fun refreshEpisodes(page: Int) {
        viewModelScope.launch {
            repository.refreshEpisodes(page)
        }
    }
}
