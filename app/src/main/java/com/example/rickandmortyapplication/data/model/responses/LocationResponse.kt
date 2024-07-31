package com.example.rickandmortyapplication.data.model.responses

import com.example.rickandmortyapplication.data.model.dto.LocationDto

data class LocationResponse(
    val results: List<LocationDto>
)
