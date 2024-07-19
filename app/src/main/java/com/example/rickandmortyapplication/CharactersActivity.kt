package com.example.rickandmortyapplication

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.ui.CharacterViewModel
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
        recyclerView.layoutManager = GridLayoutManager(this, 2) // 2 столбца

        lifecycleScope.launch {
            viewModel.allCharacters.collect { characters ->
                adapter.submitList(characters)
            }
        }

        // загрузка данных при запуске приложения
        viewModel.refreshCharacters(1)
    }
}
