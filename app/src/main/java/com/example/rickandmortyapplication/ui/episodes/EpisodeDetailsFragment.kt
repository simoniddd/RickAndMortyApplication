package com.example.rickandmortyapplication.ui.episodes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.rickandmortyapplication.data.AppDatabase
import com.example.rickandmortyapplication.data.network.RetrofitInstance
import com.example.rickandmortyapplication.data.repository.EpisodeRepository
import com.example.rickandmortyapplication.databinding.FragmentEpisodeDetailBinding
import kotlinx.coroutines.launch

class EpisodeDetailsFragment : Fragment() {

    private var _binding: FragmentEpisodeDetailBinding? = null
    private val binding get() = _binding!!

    private val apiService by lazy { RetrofitInstance.api }
    private val episodeDao by lazy { AppDatabase.getDatabase(requireContext()).episodeDao() }
    private val episodeRepository by lazy { EpisodeRepository(apiService, episodeDao) }

    private val episodeViewModel: EpisodeViewModel by activityViewModels {
        EpisodeViewModelFactory(requireActivity().application, episodeRepository)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEpisodeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val episodeIdString = arguments?.getString("episodeId") ?: return
        val episodeId = episodeIdString.toIntOrNull() ?: return // Convert to Int

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                episodeViewModel.getEpisodeById(episodeId).collect { episode ->
                    // Обновить UI с данными персонажа
                    binding.episodeName.text = episode.name
                    binding.episodeAirDate.text = episode.airdate
                    binding.episodeNumber.text = episode.episode
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

