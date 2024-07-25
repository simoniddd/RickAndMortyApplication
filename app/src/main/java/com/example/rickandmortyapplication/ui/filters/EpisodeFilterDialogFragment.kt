package com.example.rickandmortyapplication.ui.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.rickandmortyapplication.databinding.FragmentEpisodeFilterDialogBinding

class EpisodeFilterDialogFragment : DialogFragment() {

    private var _binding: FragmentEpisodeFilterDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEpisodeFilterDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {super.onViewCreated(view, savedInstanceState)

        binding.applyButton.setOnClickListener {
            // 1. Collect filter data
            val name = binding.nameEditText.text.toString()
            val episode = binding.episodeEditText.text.toString()
            val filterData = EpisodeFilterData(name, episode)

            // 2. Pass filter data to the listener
            (parentFragment as? EpisodeFilterListener)?.onEpisodeFiltersApplied(filterData)

            // 3. Dismiss the dialog
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface EpisodeFilterListener {
        fun onEpisodeFiltersApplied(filters: EpisodeFilterData)
    }

    data class EpisodeFilterData(
        val name: String,
        val episode: String
    )
}