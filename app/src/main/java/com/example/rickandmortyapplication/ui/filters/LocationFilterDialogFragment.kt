package com.example.rickandmortyapplication.ui.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.example.rickandmortyapplication.R
import com.example.rickandmortyapplication.databinding.FragmentLocationFilterDialogBinding
import com.example.rickandmortyapplication.ui.filters.EpisodeFilterDialogFragment.EpisodeFilterData
import com.example.rickandmortyapplication.ui.filters.EpisodeFilterDialogFragment.EpisodeFilterListener

class LocationFilterDialogFragment : DialogFragment() {

    private var _binding: FragmentLocationFilterDialogBinding? = null
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
        _binding = FragmentLocationFilterDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.applyButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val type = binding.typeEditText.text.toString() // Get value from EditText
            val dimension = binding.dimensionEditText.text.toString() // Get value from EditText

            val filterData = LocationFilterData(name, type, dimension)

            (parentFragment as? LocationFilterListener)?.onLocationFiltersApplied(filterData)

            dismiss()}

        binding.clearFiltersButton.setOnClickListener {
            // Clear all input fields
            binding.nameEditText.text!!.clear()
            binding.typeEditText.text!!.clear()
            binding.dimensionEditText.text!!.clear()

            // Pass empty filter data to the listener
            val filterData = LocationFilterData("", "", "")
            (parentFragment as? LocationFilterListener)?.onLocationFiltersApplied(filterData)

            // Dismiss the dialog
            dismiss()
        }
    }

    override fun onDestroyView() {super.onDestroyView()
        _binding = null
    }

    interface LocationFilterListener {
        fun onLocationFiltersApplied(filters: LocationFilterData)
        fun onClearFilters()
    }

    data class LocationFilterData(
        val name: String,
        val type: String,
        val dimension: String
    )
}