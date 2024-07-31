package com.example.rickandmortyapplication.data.model.responses

import com.example.rickandmortyapplication.data.model.dto.EpisodeDTO

data class EpisodeResponse(
    val results: List<EpisodeDTO>
)
