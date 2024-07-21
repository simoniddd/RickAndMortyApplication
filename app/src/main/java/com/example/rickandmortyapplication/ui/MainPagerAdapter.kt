package com.example.rickandmortyapplication.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapp.ui.episodes.EpisodeFragment
import com.example.myapp.ui.locations.LocationsFragment

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
