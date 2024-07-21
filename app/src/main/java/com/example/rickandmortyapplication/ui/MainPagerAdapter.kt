package com.example.rickandmortyapplication.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.rickandmortyapplication.ui.episodes.EpisodeFragment
import com.example.rickandmortyapplication.ui.locations.listLocations.LocationsFragment
import com.example.rickandmortyapplication.ui.character.CharacterFragment

class MainPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CharacterFragment()
            1 -> EpisodeFragment()
            2 -> LocationsFragment()
            else -> throw IllegalStateException("Unexpected $position")
        }
    }
}
