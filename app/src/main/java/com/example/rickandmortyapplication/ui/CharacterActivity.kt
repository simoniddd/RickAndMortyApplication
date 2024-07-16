package com.example.myapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmortyapplication.R
import com.example.rickandmortyapplication.ui.CharacterAdapter
import kotlinx.coroutines.launch

class CharactersActivity : AppCompatActivity() {
    private val viewModel: CharacterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_characters)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = CharacterAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Используем lifecycleScope для сбора данных из Flow
        lifecycleScope.launch {
            viewModel.allCharacters.collect { characters ->
                adapter.submitList(characters)
            }
        }

        // Загрузить данные при запуске
        viewModel.refreshCharacters(1)
    }
}
