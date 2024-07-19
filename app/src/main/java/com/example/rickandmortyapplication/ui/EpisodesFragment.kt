package com.example.myapp.ui.episodes

import EpisodeRepository
import EpisodesAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rickandmortyapplication.data.network.RetrofitInstance
import com.example.rickandmortyapplication.databinding.FragmentEpisodesBinding


class EpisodesFragment : Fragment() {

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

        val adapter = EpisodesAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)

        // Настройка SearchView
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

        // Наблюдение за изменениями в отфильтрованных эпизодах
        lifecycleScope.launchWhenStarted {
            episodeViewModel.filteredEpisodes.collect { episodes ->
                adapter.submitList(episodes)
            }

        // Handle swipe to refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            episodeViewModel.refreshEpisodes(page = 1)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        // Observe episode list
        episodeViewModel.allEpisodes.observe(viewLifecycleOwner) { episodes ->
            episodes?.let { adapter.submitList(it) }
        }

        // Set item click listener
        adapter.setOnItemClickListener { episode ->
            val action = EpisodesFragmentDirections.actionEpisodesFragmentToEpisodeDetailFragment(episode.id)
            findNavController().navigate(action)
        }
    }
}

