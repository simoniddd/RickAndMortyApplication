package com.example.rickandmortyapplication.ui.filters.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.rickandmortyapplication.R
import com.example.rickandmortyapplication.databinding.FragmentLocationFilterDialogBinding

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
            val type = binding.typeEditText.text.toString()
            val dimension = binding.dimensionEditText.text.toString()
            val filterData = LocationFilterData(name, type, dimension)
            (parentFragment as? LocationFilterListener)?.onLocationFiltersApplied(filterData)
            dismiss()
        }

        binding.clearFiltersButton.setOnClickListener {
            binding.nameEditText.text!!.clear()
            binding.typeEditText.text!!.clear()
            binding.dimensionEditText.text!!.clear()
            val filterData = LocationFilterData("", "", "")
            (parentFragment as? LocationFilterListener)?.onLocationFiltersApplied(filterData)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
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