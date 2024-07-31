package com.example.rickandmortyapplication.ui.character.list.fragment

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
import com.example.rickandmortyapplication.ui.character.adapter.CharacterAdapter
import com.example.rickandmortyapplication.ui.character.list.CharacterUiState
import com.example.rickandmortyapplication.ui.character.list.vm.CharacterViewModel
import com.example.rickandmortyapplication.ui.character.list.vmfactory.CharacterViewModelFactory
import com.example.rickandmortyapplication.ui.filters.character.CharacterFilterDialogFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class CharacterFragment : Fragment(), CharacterFilterDialogFragment.CharacterFilterListener {

    private lateinit var binding: FragmentCharactersBinding
    private val characterDao by lazy { AppDatabase.getDatabase(requireContext()).characterDao() }
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
        val lottieAnimationView = binding.lottieAnimationView

        viewLifecycleOwner.lifecycleScope.launch {
            characterViewModel.characterUiState.collect { state ->
                when (state) {
                    is CharacterUiState.Loading -> {
                        lottieAnimationView.visibility = View.VISIBLE
                        lottieAnimationView.playAnimation()
                    }

                    is CharacterUiState.Success -> {
                        characterAdapter.submitList(state.characters)
                        lottieAnimationView.visibility = View.GONE
                        lottieAnimationView.pauseAnimation()
                    }

                    is CharacterUiState.Error -> {
                        lottieAnimationView.visibility = View.GONE
                        lottieAnimationView.pauseAnimation()
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    characterViewModel.loadNextPage()
                }
            }
        })

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

        characterAdapter.setOnItemClickListener { character ->
            val action = CharacterFragmentDirections
                .actionCharactersFragmentToCharacterDetailsFragment(character.id.toString())
            findNavController().navigate(action)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            characterViewModel.setSearchQuery("")
            characterViewModel.loadCharacters(characterViewModel._filters.value, "")
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.filterButton.setOnClickListener {
            val dialog = CharacterFilterDialogFragment()
            dialog.show(childFragmentManager, "CharacterFilterDialog")
        }
    }

    override fun onCharacterFiltersCleared() {
        characterViewModel.clearFilters()
    }

    override fun onCharacterFiltersApplied(filters: CharacterFilterDialogFragment.CharacterFilterData) {
        characterViewModel.applyFilters(filters)
    }
}