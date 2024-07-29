package com.example.rickandmortyapplication.ui.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.rickandmortyapplication.data.AppDatabase
import com.example.rickandmortyapplication.data.network.RetrofitInstance
import com.example.rickandmortyapplication.data.repository.CharacterRepository
import com.example.rickandmortyapplication.databinding.FragmentCharacterDetailsBinding
import com.example.rickandmortyapplication.ui.episodes.EpisodeAdapter
import kotlinx.coroutines.launch

class CharacterDetailsFragment : Fragment() {

    private var _binding: FragmentCharacterDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var episodeAdapter: EpisodeAdapter
    private lateinit var characterDetailsViewModel: CharacterDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharacterDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val characterIdString = arguments?.getString("characterId") ?: return
        val characterId = characterIdString.toIntOrNull() ?: return

        // Initialize ViewModel with the factory
        val application = requireActivity().application
        val characterDao = AppDatabase.getDatabase(application).characterDao()
        val apiService = RetrofitInstance.api
        val characterRepository = CharacterRepository(apiService, characterDao)
        val factory = CharacterDetailsViewModelFactory(characterRepository)
        characterDetailsViewModel = ViewModelProvider(this, factory).get(CharacterDetailsViewModel::class.java)

        setupRecyclerView()
        observeViewModel()

        // Fetch character details
        characterId?.let { characterDetailsViewModel.getCharacterDetails(it) }

        // Set click listener for origin TextView
        binding.characterOrigin.setOnClickListener {
            when (val uiState = characterDetailsViewModel.characterUiState.value) {
                is CharacterDetailsUiState.Success -> {
                    // Теперь вы можете безопасно получить доступ к `character`
                    val originId = uiState.character.origin.getLocationId()
                    val action = CharacterDetailsFragmentDirections
                        .actionCharacterDetailsFragmentToLocationDetailsFragment(originId.toString())
                    findNavController().navigate(action)
                }

                else -> {
                    // Обработка других состояний, таких как Loading или Error
                    Toast.makeText(
                        requireContext(),
                        "Unable to navigate, data not available",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        episodeAdapter = EpisodeAdapter().apply {
            setOnItemClickListener { episode ->
                val action = CharacterDetailsFragmentDirections
                    .actionCharacterDetailsFragmentToEpisodeDetailsFragment(episode.id.toString())
                findNavController().navigate(action)
            }
        }
        binding.episodesRecyclerView.apply {
            adapter = episodeAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                characterDetailsViewModel.characterUiState.collect { uiState ->
                    when (uiState) {
                        is CharacterDetailsUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is CharacterDetailsUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.characterName.text = uiState.character.name
                            binding.characterStatus.text = uiState.character.status
                            binding.characterSpecies.text = uiState.character.species
                            binding.characterGender.text = uiState.character.gender
                            binding.characterOrigin.text = uiState.character.origin.name
                            binding.characterLocation.text = uiState.character.location.name

                            Glide.with(this@CharacterDetailsFragment)
                                .load(uiState.character.image)
                                .into(binding.characterImage)

                            episodeAdapter.submitList(uiState.episodes)
                        }
                        is CharacterDetailsUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(requireContext(), uiState.message, Toast.LENGTH_SHORT).show()
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

