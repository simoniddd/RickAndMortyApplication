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
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmortyapplication.data.AppDatabase
import com.example.rickandmortyapplication.data.repository.LocationRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class LocationsFragment : Fragment() {

    private lateinit var binding: FragmentLocationsBinding
    private val locationDao by lazy { AppDatabase.getDatabase(requireContext()).locationDao()}
    private val apiService by lazy { RetrofitInstance.api }
    private val locationRepository by lazy { LocationRepository(apiService, locationDao) }
    private val locationViewModel: LocationsViewModel by viewModels {
        LocationViewModelFactory(requireActivity().application, locationRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = LocationsAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)

        viewLifecycleOwner.lifecycleScope.launch {
            locationViewModel.locationUiState.collect { state ->
                when (state) {
                    is LocationUiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is LocationUiState.Success -> {
                        adapter.submitList(state.locations)
                        binding.progressBar.visibility = View.GONE
                    }
                    is LocationUiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        // Show error message (e.g., Snackbar)
                        // Snackbar.make(binding.root, state.message, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    locationViewModel.loadNextPage()
                }
            }
        })

        adapter.setOnItemClickListener { location ->
            val action = LocationsFragmentDirections
                .actionLocationsFragmentToLocationDetailsFragment(location.id.toString())
            findNavController().navigate(action)
        }

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

        binding.swipeRefreshLayout.setOnRefreshListener {
            locationViewModel.setSearchQuery("")
            locationViewModel.loadLocations()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }
}
