package com.example.rickandmortyapplication.ui.episodes.details.vmfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rickandmortyapplication.data.repository.EpisodeRepository
import com.example.rickandmortyapplication.ui.episodes.details.vm.EpisodeDetailsViewModel

class EpisodeDetailsViewModelFactory(
    private val repository: EpisodeRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EpisodeDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EpisodeDetailsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}