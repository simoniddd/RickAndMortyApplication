package com.example.rickandmortyapplication.ui.episodes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.rickandmortyapplication.databinding.FragmentEpisodeDetailBinding
import kotlinx.coroutines.launch

class EpisodeDetailsFragment : Fragment() {

    private var _binding: FragmentEpisodeDetailBinding? = null
    private val binding get() = _binding!!
    private val episodeViewModel: EpisodeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEpisodeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val episodeId = arguments?.getInt("episodeId") ?: return

        episodeViewModel.setEpisodeId(episodeId)
        episodeViewModel.getEpisodeById(episodeId)

        viewLifecycleOwner.lifecycleScope.launch {
            episodeViewModel.episodeDetail.collect { episode ->
                episode?.let {
                    binding.episodeName.text = it.name
                    binding.episodeAirDate.text = it.airdate
                    binding.episodeUrl.text = it.url
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

