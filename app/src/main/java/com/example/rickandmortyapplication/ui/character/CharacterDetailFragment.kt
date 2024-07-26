package com.example.rickandmortyapplication.ui.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.rickandmortyapplication.data.AppDatabase
import com.example.rickandmortyapplication.data.network.RetrofitInstance
import com.example.rickandmortyapplication.data.repository.CharacterRepository
import com.example.rickandmortyapplication.databinding.FragmentCharacterDetailsBinding
import kotlinx.coroutines.launch

class CharacterDetailFragment : Fragment() {

    private var _binding: FragmentCharacterDetailsBinding? = null
    private val binding get() = _binding!!

    private val characterViewModel: CharacterViewModel by activityViewModels {
        CharacterViewModelFactory(requireActivity().application, CharacterRepository(
            RetrofitInstance.api, AppDatabase.getDatabase(requireContext()).characterDao())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCharacterDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val characterIdString = arguments?.getString("characterId") ?: return
        val characterId = characterIdString.toIntOrNull() ?: return // Convert to Int

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                characterViewModel.getCharacter(characterId).collect { character ->
                    // Update UI with character details
                    binding.characterName.text = character.name
                    binding.characterSpecies.text = character.species
                    binding.characterStatus.text = character.status
                    binding.characterGender.text = character.gender
                    binding.characterOrigin.text = character.origin.name
                    binding.characterLocation.text = character.location.name
                    binding.characterEpisodes.text = character.episode.joinToString(", ") // Join episodes list
                    Glide.with(this@CharacterDetailFragment)
                        .load(character.image)
                        .into(binding.characterImage)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

