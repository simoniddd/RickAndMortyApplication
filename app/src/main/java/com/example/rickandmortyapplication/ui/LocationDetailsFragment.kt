package com.example.rickandmortyapplication.ui

import LocationRepository
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.myapp.ui.episodes.EpisodeViewModel
import com.example.myapp.ui.locations.LocationViewModel
import com.example.myapp.ui.locations.LocationViewModelFactory
import com.example.rickandmortyapplication.databinding.FragmentEpisodeDetailBinding
import com.example.rickandmortyapplication.databinding.FragmentLocationDetailsBinding
import kotlinx.coroutines.launch

class LocationDetailsFragment : Fragment() {

    private var _binding: FragmentLocationDetailsBinding? = null
    private val binding get() = _binding!!
    private val locationViewModel: LocationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocationDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val locationId = arguments?.getInt("locationId") ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            locationViewModel.getLocationById(locationId).collect { location ->
                location?.let {
                    binding.locationName.text = it.name
                    binding.locationType.text = it.type
                    binding.locationDimension.text = it.dimension
                    // Привязка других данных, если нужно
                }
            }
        }
    }
}
