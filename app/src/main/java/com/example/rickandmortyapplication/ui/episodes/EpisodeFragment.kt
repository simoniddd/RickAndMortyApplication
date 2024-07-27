package com.example.rickandmortyapplication.ui.episodes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmortyapplication.data.AppDatabase
import com.example.rickandmortyapplication.data.network.RetrofitInstance
import com.example.rickandmortyapplication.data.repository.EpisodeRepository
import com.example.rickandmortyapplication.databinding.FragmentEpisodesBinding
import com.example.rickandmortyapplication.ui.filters.EpisodeFilterDialogFragment
import com.example.rickandmortyapplication.ui.filters.LocationFilterDialogFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class EpisodeFragment : Fragment(), EpisodeFilterDialogFragment.EpisodeFilterListener {

    private lateinit var binding: FragmentEpisodesBinding
    private val episodeDao by lazy { AppDatabase.getDatabase(requireContext()).episodeDao() }
    private val apiService by lazy { RetrofitInstance.api }
    private val episodeRepository by lazy { EpisodeRepository(apiService, episodeDao) }
    private val episodeViewModel: EpisodeViewModel by viewModels {
        EpisodeViewModelFactory(requireActivity().application, episodeRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEpisodesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = EpisodeAdapter()
        binding.episodesRecyclerView.adapter = adapter
        binding.episodesRecyclerView.layoutManager = GridLayoutManager(context, 2)

        // Observe episode UI state
        viewLifecycleOwner.lifecycleScope.launch {
            episodeViewModel.episodeUiState.collectLatest { state ->
                when (state) {
                    is EpisodeUiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is EpisodeUiState.Success -> {
                        adapter.submitList(state.episodes)
                        binding.progressBar.visibility = View.GONE
                    }
                    is EpisodeUiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        // Show error message (e.g., Snackbar)
                        // Snackbar.make(binding.root, state.message, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Open filter dialog
        binding.filterButton.setOnClickListener {
            val dialog = EpisodeFilterDialogFragment()
            dialog.show(childFragmentManager, "EpisodeFilterDialog")
        }

        // Handle scrolling for pagination
        binding.episodesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    episodeViewModel.loadNextPage()
                }
            }
        })

        // Set item click listener
        adapter.setOnItemClickListener { episode ->
            val action = EpisodeFragmentDirections
                .actionEpisodesFragmentToEpisodeDetailsFragment(episode.id.toString())
            findNavController().navigate(action)
        }

        // Handle search query
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { episodeViewModel.setSearchQuery(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { episodeViewModel.setSearchQuery(it) }
                return true
            }
        })

        // Handle swipe to refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            episodeViewModel.setSearchQuery("") // Reset search query
            episodeViewModel.loadEpisodes() // Reload episodes (starts from page 1)
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onEpisodeFiltersApplied(filters: EpisodeFilterDialogFragment.EpisodeFilterData) {
        episodeViewModel.setFilters(
            name = filters.name,
            episode = filters.episode
        )
    }
}




