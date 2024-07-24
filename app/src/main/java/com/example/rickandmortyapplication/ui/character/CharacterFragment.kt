package com.example.rickandmortyapplication.ui.character

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rickandmortyapplication.data.AppDatabase
import com.example.rickandmortyapplication.data.network.RetrofitInstance
import com.example.rickandmortyapplication.data.repository.CharacterRepository
import com.example.rickandmortyapplication.databinding.FragmentCharactersBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CharacterFragment : Fragment() {

    private lateinit var binding: FragmentCharactersBinding
    private val characterDao by lazy { AppDatabase.getDatabase(requireContext()).characterDao() }
    private val apiService by lazy { RetrofitInstance.api }
    private val characterRepository by lazy { CharacterRepository(apiService, characterDao) }
    private val characterViewModel: CharacterViewModel by viewModels {
        CharacterViewModelFactory(requireActivity().application, characterRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharactersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("CharacterFragment", "onViewCreated called")
        val adapter = CharacterAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)

        //приведение первых данных из репозитория на экран
        lifecycleScope.launch {
            characterViewModel.allCharacters.collectLatest { characters ->
                adapter.submitList(characters)
            }
        }

        // Обработка ввода в SearchView
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    Log.d("CharacterFragment", "Search query submitted: $it")
                    characterViewModel.setSearchQuery(it) }
                return true
            }
        // Изменение поискового запроса
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    Log.d("CharacterFragment", "Search query changed: $it")
                    characterViewModel.setSearchQuery(it) }
                return true
            }
        })

        adapter.setOnItemClickListener { character ->
            val action = CharacterFragmentDirections
                .actionCharactersFragmentToCharacterDetailsFragment(
                character.id.toString()
            )
            findNavController().navigate(action)
        }

        // Handle swipe to refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            Log.d("CharacterFragment", "Swipe to refresh")
            characterViewModel.refreshCharacters(page = 1)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        // Наблюдение за фильтрованными результатами
        lifecycleScope.launch {
            characterViewModel.filteredCharacters.collect { characters ->
                Log.d("CharacterFragment", "Filtered characters collected: ${characters.size}")
                adapter.submitList(characters)
            }
        }
    }
}