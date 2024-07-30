package com.example.rickandmortyapplication.ui.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.rickandmortyapplication.R
import com.example.rickandmortyapplication.databinding.FragmentEpisodeFilterDialogBinding

class EpisodeFilterDialogFragment : DialogFragment() {

    private var _binding: FragmentEpisodeFilterDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEpisodeFilterDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.applyButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val episode = binding.episodeEditText.text.toString()
            val filterData = EpisodeFilterData(name, episode)
            (parentFragment as? EpisodeFilterListener)?.onEpisodeFiltersApplied(filterData)
            dismiss()
        }

        binding.clearFiltersButton.setOnClickListener {
            binding.nameEditText.text?.clear()
            binding.episodeEditText.text?.clear()
            val filterData = EpisodeFilterData("", "")
            (parentFragment as? EpisodeFilterListener)?.onEpisodeFiltersApplied(filterData)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface EpisodeFilterListener {
        fun onEpisodeFiltersApplied(filters: EpisodeFilterData)
        fun onClearFilters()
    }

    data class EpisodeFilterData(
        val name: String,
        val episode: String
    )
}
