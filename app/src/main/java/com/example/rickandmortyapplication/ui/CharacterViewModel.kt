package com.example.myapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.data.repository.CharacterRepository
import com.example.rickandmortyapplication.data.database.entities.CharacterEntity
import com.example.rickandmortyapplication.data.network.RetrofitInstance.api
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CharacterViewModel(application: Application) : AndroidViewModel(application) {
    private val characterDao = AppDatabase.getDatabase(application).characterDao()
    private val repository = CharacterRepository(api, characterDao)

    val allCharacters: Flow<List<CharacterEntity>> = repository.getAllCharacters()

    fun refreshCharacters(page: Int) {
        viewModelScope.launch {
            repository.refreshCharacters(page)
        }
    }
}