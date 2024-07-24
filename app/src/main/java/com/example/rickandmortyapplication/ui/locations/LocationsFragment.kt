package com.example.rickandmortyapplication.ui.locations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rickandmortyapplication.data.network.RetrofitInstance
import com.example.rickandmortyapplication.databinding.FragmentLocationsBinding
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.rickandmortyapplication.data.AppDatabase
import com.example.rickandmortyapplication.data.repository.LocationRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class LocationsFragment : Fragment() {

    private lateinit var binding: FragmentLocationsBinding
    private val locationDao by lazy { AppDatabase.getDatabase(requireContext()).locationDao() }
    private val apiService by lazy { RetrofitInstance.api }
    private val locationRepository by lazy { LocationRepository(apiService, locationDao) }
    private val locationViewModel: LocationsViewModel by viewModels {
        LocationViewModelFactory(requireActivity().application, locationRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLocationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = LocationsAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)

        // Adding first info on screen
        lifecycleScope.launch {
            locationViewModel.allLocations.collectLatest { characters ->
                adapter.submitList(characters)
            }
        }

        // Set item click listener
        adapter.setOnItemClickListener { location ->
            val action = LocationsFragmentDirections
                .actionLocationsFragmentToLocationDetailsFragment(
                location.id.toString()
            )
            findNavController().navigate(action)
        }

        // Observe filtered location list using lifecycleScope
        lifecycleScope.launch {
            locationViewModel.filteredLocations.collect { locations ->
                adapter.submitList(locations)
            }
        }

        // Handle search query
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { locationViewModel.setSearchQuery(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { locationViewModel.setSearchQuery(it) }
                return true
            }
        })

        // Handle swipe to refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            locationViewModel.refreshLocations(page = 1)
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }
}
