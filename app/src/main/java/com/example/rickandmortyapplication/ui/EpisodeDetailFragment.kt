package com.example.rickandmortyapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.myapp.ui.episodes.EpisodeViewModel
import com.example.rickandmortyapplication.databinding.FragmentEpisodeDetailBinding

class EpisodeDetailFragment : Fragment() {

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

        episodeViewModel.getEpisodeById(episodeId)

        episodeViewModel.episodeDetail.observe(viewLifecycleOwner) { episode ->
            episode?.let {
                binding.episodeName.text = it.name
                binding.episodeAirDate.text = it.airdate
                binding.episodeUrl.text = it.url
                // Привязка других данных
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
