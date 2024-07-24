package com.example.rickandmortyapplication.ui.character

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rickandmortyapplication.data.repository.CharacterRepository

class CharacterViewModelFactory(
    private val application: Application,
    private val repository: CharacterRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d("CharacterViewModelFactory", "Creating ViewModel: $modelClass with repository: $repository")
        if (modelClass.isAssignableFrom(CharacterViewModel::class.java)) {
            return CharacterViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
