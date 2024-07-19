package com.example.rickandmortyapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapp.data.repository.CharacterRepository
import com.example.myapp.ui.CharacterViewModel
import com.example.myapp.ui.CharacterViewModelFactory
import com.example.rickandmortyapplication.data.network.RetrofitInstance
import com.example.rickandmortyapplication.databinding.FragmentCharactersBinding
import com.example.rickandmortyapplication.ui.CharacterAdapter

class CharactersFragment : Fragment() {

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
    ): View? {
        binding = FragmentCharactersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = CharacterAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)

        // Обработка ввода в SearchView
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { characterViewModel.setSearchQuery(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { characterViewModel.setSearchQuery(it) }
                return true
            }
        })

        // Handle swipe to refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            characterViewModel.refreshCharacters(page = 1)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        // Наблюдение за фильтрованными результатами
        lifecycleScope.launchWhenStarted {
            characterViewModel.filteredCharacters.collect { characters ->
                adapter.submitList(characters)
            }
        }
    }
}