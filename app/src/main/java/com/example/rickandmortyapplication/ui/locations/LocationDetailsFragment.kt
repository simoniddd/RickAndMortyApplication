package com.example.rickandmortyapplication.ui.locations

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
import com.example.rickandmortyapplication.data.repository.LocationRepository
import com.example.rickandmortyapplication.databinding.FragmentEpisodeDetailBinding
import com.example.rickandmortyapplication.databinding.FragmentLocationDetailsBinding
import com.example.rickandmortyapplication.ui.episodes.EpisodeViewModel
import com.example.rickandmortyapplication.ui.episodes.EpisodeViewModelFactory
import kotlinx.coroutines.launch

class LocationDetailsFragment : Fragment() {

    private var _binding: FragmentLocationDetailsBinding? = null
    private val binding get() = _binding!!

    private val apiService by lazy { RetrofitInstance.api }
    private val locationDao by lazy { AppDatabase.getDatabase(requireContext()).locationDao() }
    private val locationRepository by lazy { LocationRepository(apiService, locationDao) }

    private val locationViewModel: LocationsViewModel by activityViewModels {
        LocationViewModelFactory(requireActivity().application, locationRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocationDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val locationIdString = arguments?.getString("locationId") ?: return
        val locationId = locationIdString.toIntOrNull() ?: return // Convert to Int

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                locationViewModel.getLocationById(locationId).collect { location ->
                    // Обновить UI с данными персонажа
                    binding.locationName.text = location.name
                    binding.locationType.text = location.type
                    binding.locationDimension.text = location.dimension
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
