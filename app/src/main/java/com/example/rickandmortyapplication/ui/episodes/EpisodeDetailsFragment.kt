package com.example.rickandmortyapplication.ui.episodes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rickandmortyapplication.data.AppDatabase
import com.example.rickandmortyapplication.data.network.RetrofitInstance
import com.example.rickandmortyapplication.data.repository.EpisodeRepository
import com.example.rickandmortyapplication.databinding.FragmentEpisodeDetailsBinding
import com.example.rickandmortyapplication.ui.character.CharacterAdapter
import kotlinx.coroutines.launch

class EpisodeDetailsFragment : Fragment() {

    private var _binding: FragmentEpisodeDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var characterAdapter: CharacterAdapter
    private lateinit var episodeDetailsViewModel: EpisodeDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEpisodeDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val episodeIdString = arguments?.getString("episodeId") ?: return
        val episodeId = episodeIdString.toIntOrNull() ?: return
        val application = requireActivity().application
        val episodeDao = AppDatabase.getDatabase(application).episodeDao()
        val apiService = RetrofitInstance.api
        val episodeRepository = EpisodeRepository(apiService, episodeDao)
        val factory = EpisodeDetailsViewModelFactory(episodeRepository)
        episodeDetailsViewModel =
            ViewModelProvider(this, factory).get(EpisodeDetailsViewModel::class.java)

        setupRecyclerView()
        observeViewModel()

        episodeId.let { episodeDetailsViewModel.getEpisodeDetails(it) }
    }

    private fun setupRecyclerView() {
        characterAdapter = CharacterAdapter().apply {
            setOnItemClickListener { character ->
                val action = EpisodeDetailsFragmentDirections
                    .actionEpisodeDetailsFragmentToCharacterDetailsFragment(character.id.toString())
                findNavController().navigate(action)
            }
        }
        binding.charactersRecyclerView.apply {
            adapter = characterAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                episodeDetailsViewModel.episodeUiState.collect { uiState ->
                    when (uiState) {
                        is EpisodeDetailsUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is EpisodeDetailsUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.episodeName.text = uiState.episode.name
                            binding.episodeAirDate.text = uiState.episode.air_date
                            binding.episodeCode.text = uiState.episode.episode
                            characterAdapter.submitList(uiState.characters)
                        }

                        is EpisodeDetailsUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(requireContext(), uiState.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

