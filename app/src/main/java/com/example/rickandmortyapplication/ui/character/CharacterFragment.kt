package com.example.rickandmortyapplication.ui.character

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
import com.example.rickandmortyapplication.data.repository.CharacterRepository
import com.example.rickandmortyapplication.databinding.FragmentCharactersBinding
import kotlinx.coroutines.launch

class CharacterFragment : Fragment() {

    private lateinit var binding: FragmentCharactersBinding
    private val characterDao by lazy { AppDatabase.getDatabase(requireContext()).characterDao()}
    private val apiService by lazy { RetrofitInstance.api }
    private val characterRepository by lazy { CharacterRepository(apiService, characterDao) }
    private val characterViewModel: CharacterViewModel by viewModels {
        CharacterViewModelFactory(requireActivity().application, characterRepository)
    }
    private val characterAdapter = CharacterAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharactersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = characterAdapter
        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)

        viewLifecycleOwner.lifecycleScope.launch {
            characterViewModel.characterUiState.collect { state ->
                when (state) {
                    is CharacterUiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is CharacterUiState.Success -> {
                        characterAdapter.submitList(state.characters)
                        binding.progressBar.visibility = View.GONE
                    }
                    is CharacterUiState.Error -> {
                        binding.progressBar.visibility = View.GONE

                    }
                }
            }
        }

        // Handle scrolling for pagination
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    characterViewModel.loadNextPage()
                }
            }
        })

        // Handle search view
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

        // Handle item click
        characterAdapter.setOnItemClickListener { character ->
            val action = CharacterFragmentDirections
                .actionCharactersFragmentToCharacterDetailsFragment(character.id.toString())
            findNavController().navigate(action)
        }

        // Handle swipe to refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            characterViewModel.setSearchQuery("") // Reset search query
            characterViewModel.refreshCharacters() // Reload characters (starts from page 1)
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }
}