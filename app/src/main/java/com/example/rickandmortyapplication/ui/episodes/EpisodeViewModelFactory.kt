package com.example.rickandmortyapplication.ui.episodes

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rickandmortyapplication.data.repository.EpisodeRepository

class EpisodeViewModelFactory(
    private val application: Application,
    private val repository: EpisodeRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST_EPISODE")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EpisodeViewModel::class.java)) {
            return EpisodeViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
