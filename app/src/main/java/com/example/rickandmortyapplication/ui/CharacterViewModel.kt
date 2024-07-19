package com.example.myapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.data.repository.CharacterRepository
import com.example.rickandmortyapplication.data.database.entities.CharacterEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CharacterViewModel(
    application: Application,
    private val repository: CharacterRepository
) : AndroidViewModel(application) {

    val allCharacters: Flow<List<CharacterEntity>> = repository.getAllCharacters()

    fun refreshCharacters(page: Int) {
        viewModelScope.launch {
            repository.refreshCharacters(page)
        }
    }
}
