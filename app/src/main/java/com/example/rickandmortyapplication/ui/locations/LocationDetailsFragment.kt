package com.example.rickandmortyapplication.ui.locations

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
import com.example.rickandmortyapplication.data.model.LocationDto
import com.example.rickandmortyapplication.data.model.CharacterDto
import com.example.rickandmortyapplication.ui.locations.LocationUiState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rickandmortyapplication.data.AppDatabase
import com.example.rickandmortyapplication.data.network.RetrofitInstance
import com.example.rickandmortyapplication.data.repository.LocationRepository
import com.example.rickandmortyapplication.databinding.FragmentLocationDetailsBinding
import com.example.rickandmortyapplication.ui.character.CharacterAdapter
import kotlinx.coroutines.launch

class LocationDetailsFragment : Fragment() {

    private var _binding: FragmentLocationDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var characterAdapter: CharacterAdapter
    private lateinit var locationDetailsViewModel: LocationDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val locationIdString = arguments?.getString("locationId") ?: return
        val locationId = locationIdString.toIntOrNull() ?: return

        // Initialize ViewModel with the factory
        val application = requireActivity().application
        val locationDao = AppDatabase.getDatabase(application).locationDao()
        val apiService = RetrofitInstance.api
        val locationRepository = LocationRepository(apiService, locationDao)
        val factory = LocationDetailsViewModelFactory(locationRepository)
        locationDetailsViewModel = ViewModelProvider(this, factory).get(LocationDetailsViewModel::class.java)

        setupRecyclerView()
        observeViewModel()

        // Fetch location details
        locationId?.let { locationDetailsViewModel.getLocationDetails(it) }
    }

    private fun setupRecyclerView() {
        characterAdapter = CharacterAdapter().apply {
            setOnItemClickListener { character ->
                val action = LocationDetailsFragmentDirections
                    .actionLocationDetailsFragmentToCharacterDetailsFragment(character.id.toString())
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
                locationDetailsViewModel.locationUiState.collect { uiState ->
                    when (uiState) {
                        is LocationDetailsUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is LocationDetailsUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.locationName.text = uiState.location.name
                            binding.locationType.text = uiState.location.type
                            binding.locationDimension.text = uiState.location.dimension
                            characterAdapter.submitList(uiState.characters)
                        }
                        is LocationDetailsUiState.Error -> {
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
