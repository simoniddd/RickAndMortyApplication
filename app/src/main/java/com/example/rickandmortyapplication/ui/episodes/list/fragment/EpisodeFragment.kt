package com.example.rickandmortyapplication.ui.episodes.list.fragment

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
import com.example.rickandmortyapplication.ui.episodes.adapter.EpisodeAdapter
import com.example.rickandmortyapplication.ui.episodes.list.EpisodeUiState
import com.example.rickandmortyapplication.ui.episodes.list.vm.EpisodeViewModel
import com.example.rickandmortyapplication.ui.episodes.list.vmfactory.EpisodeViewModelFactory
import com.example.rickandmortyapplication.ui.filters.episode.EpisodeFilterDialogFragment
import com.google.android.material.snackbar.Snackbar
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
    ): View {
        binding = FragmentEpisodesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = EpisodeAdapter()
        binding.episodesRecyclerView.adapter = adapter
        binding.episodesRecyclerView.layoutManager = GridLayoutManager(context, 2)

        val lottieAnimationView = binding.lottieAnimationView

        viewLifecycleOwner.lifecycleScope.launch {
            episodeViewModel.episodeUiState.collectLatest { state ->
                when (state) {
                    is EpisodeUiState.Loading -> {
                        lottieAnimationView.visibility = View.VISIBLE
                        lottieAnimationView.playAnimation()
                    }

                    is EpisodeUiState.Success -> {
                        adapter.submitList(state.episodes)
                        lottieAnimationView.visibility = View.GONE
                        lottieAnimationView.pauseAnimation()
                    }

                    is EpisodeUiState.Error -> {
                        lottieAnimationView.visibility = View.GONE
                        lottieAnimationView.pauseAnimation()
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.filterButton.setOnClickListener {
            val dialog = EpisodeFilterDialogFragment()
            dialog.show(childFragmentManager, "EpisodeFilterDialog")
        }

        binding.episodesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    episodeViewModel.loadNextPage()
                }
            }
        })

        adapter.setOnItemClickListener { episode ->
            val action = EpisodeFragmentDirections
                .actionEpisodesFragmentToEpisodeDetailsFragment(episode.id.toString())
            findNavController().navigate(action)
        }

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

        binding.swipeRefreshLayout.setOnRefreshListener {
            episodeViewModel.setSearchQuery("")
            episodeViewModel.loadEpisodes()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onEpisodeFiltersApplied(filters: EpisodeFilterDialogFragment.EpisodeFilterData) {
        episodeViewModel.setFilters(
            name = filters.name,
            episode = filters.episode
        )
    }

    override fun onClearFilters() {
        episodeViewModel.clearFilters()
    }
}




