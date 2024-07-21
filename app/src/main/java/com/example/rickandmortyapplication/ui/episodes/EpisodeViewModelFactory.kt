package com.example.rickandmortyapplication.ui.episodes

import EpisodeRepository
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EpisodeViewModelFactory(
    private val application: Application,
    private val repository: EpisodeRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast_episode")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EpisodeViewModel::class.java)) {
            return EpisodeViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
