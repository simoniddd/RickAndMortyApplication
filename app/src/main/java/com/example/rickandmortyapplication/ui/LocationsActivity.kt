package com.example.rickandmortyapplication.ui

import LocationsAdapter
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.ui.LocationViewModel
import com.example.myapp.ui.locations.LocationViewModel
import com.example.rickandmortyapplication.R
import kotlinx.coroutines.launch

class LocationsActivity : AppCompatActivity() {
    private val viewModel: LocationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locations)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = LocationsAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 2) // 2 столбца

        lifecycleScope.launch {
            viewModel.allLocations.collect { locations ->
                adapter.submitList(locations)
            }
        }

        // Загрузить данные при запуске
        viewModel.refreshLocations(1)
    }
}
