package com.example.rickandmortyapplication.ui.locations.detailLocation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rickandmortyapplication.data.repository.LocationRepository

class LocationDetailsViewModelFactory(
    private val application: Application,
    private val repository: LocationRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocationDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LocationDetailsViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
