package com.example.rickandmortyapplication.ui.locations

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rickandmortyapplication.data.network.RetrofitInstance
import com.example.rickandmortyapplication.databinding.FragmentLocationsBinding
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmortyapplication.data.AppDatabase
import com.example.rickandmortyapplication.data.repository.LocationRepository
import com.example.rickandmortyapplication.ui.filters.EpisodeFilterDialogFragment
import com.example.rickandmortyapplication.ui.filters.LocationFilterDialogFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class LocationsFragment : Fragment(), LocationFilterDialogFragment.LocationFilterListener {

    private lateinit var binding: FragmentLocationsBinding
    private val locationRepository by lazy { LocationRepository(RetrofitInstance.api, AppDatabase.getDatabase(requireContext()).locationDao()) }
    private val locationViewModel: LocationsViewModel by viewModels { LocationViewModelFactory(Application(), locationRepository) }

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
                        binding.lottieAnimationView.visibility = View.VISIBLE
                        binding.lottieAnimationView.playAnimation()
                    }
                    is LocationUiState.Success -> {
                        adapter.submitList(state.locations)
                        binding.lottieAnimationView.visibility = View.GONE
                        binding.lottieAnimationView.cancelAnimation()
                    }
                    is LocationUiState.Error -> {
                        binding.lottieAnimationView.visibility = View.GONE
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.filterButton.setOnClickListener {
            val dialog = LocationFilterDialogFragment()
            dialog.show(childFragmentManager, "LocationFilterDialog")
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
            locationViewModel.setFilters("", "", "")
            locationViewModel.loadLocations()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onLocationFiltersApplied(filters: LocationFilterDialogFragment.LocationFilterData) {
        locationViewModel.setFilters(
            name = filters.name,
            type = filters.type,
            dimension = filters.dimension
        )
    }
}


