package com.example.rickandmortyapplication.ui.character

import com.example.rickandmortyapplication.ui.episodes.EpisodeAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import com.bumptech.glide.Glide
import com.example.rickandmortyapplication.databinding.FragmentCharacterDetailsBinding
import kotlinx.coroutines.launch

class CharacterDetailFragment : Fragment() {

    private var _binding: FragmentCharacterDetailsBinding? = null
    private val binding get() = _binding!!
    private val characterViewModel: CharacterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCharacterDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val characterId = arguments?.getInt("characterId") ?: return

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                characterViewModel.getCharacter(characterId).collect { character ->
                    // Обновить UI с данными персонажа
                    binding.characterName.text = character.name
                    binding.characterSpecies.text = character.species
                    binding.characterStatus.text = character.status
                    binding.characterGender.text = character.gender
                    Glide.with(this@CharacterDetailFragment)
                        .load(character.image)
                        .into(binding.characterImage)

                    // Адаптер и RecyclerView для отображения эпизодов
                    val adapter = EpisodeAdapter()
                    binding.episodesRecyclerView.adapter = adapter
                    binding.episodesRecyclerView.layoutManager = LinearLayoutManager(context)

                    val episodeEntities = character.episodes.map { episodeUrl ->
                        async {
                            characterViewModel.getEpisodeData(episodeUrl)
                        }
                    }.awaitAll()
                    adapter.submitList(episodeEntities)
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
