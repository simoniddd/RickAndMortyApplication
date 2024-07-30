package com.example.rickandmortyapplication.ui.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.example.rickandmortyapplication.R
import com.example.rickandmortyapplication.databinding.FragmentCharacterFilterDialogBinding

class CharacterFilterDialogFragment : DialogFragment() {

    private var _binding: FragmentCharacterFilterDialogBinding? = null
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
        _binding = FragmentCharacterFilterDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up status spinner
        val statusAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.character_status_array,
            android.R.layout.simple_spinner_item
        )
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.statusSpinner.adapter = statusAdapter

        // Set up gender spinner
        val genderAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.character_gender_array,
            android.R.layout.simple_spinner_item
        )
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.genderSpinner.adapter = genderAdapter

        // Set up apply button click listener
        binding.applyButton.setOnClickListener {
            // Get filter values from UI elements
            val name = binding.nameEditText.text.toString()
            val status = binding.statusSpinner.selectedItem.toString()
            val species = binding.speciesEditText.text.toString()
            val type = binding.typeEditText.text.toString()
            val gender = binding.genderSpinner.selectedItem.toString()

            // Create FilterData object
            val filterData = CharacterFilterData(name, status, species, type, gender)

            // Pass filter data to CharacterFragment
            (parentFragment as? CharacterFilterListener)?.onCharacterFiltersApplied(filterData)

            dismiss()
        }

        // Set up clear button click listener
        binding.clearFiltersButton.setOnClickListener {
            // Clear filters and notify listener
            (parentFragment as? CharacterFilterListener)?.onCharacterFiltersCleared()

            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    interface CharacterFilterListener {
        fun onCharacterFiltersApplied(filters: CharacterFilterData)
        fun onCharacterFiltersCleared()
    }

    data class CharacterFilterData(
        val name: String,
        val status: String,
        val species: String,
        val type: String,
        val gender: String
    )
}
