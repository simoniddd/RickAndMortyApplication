package com.example.rickandmortyapplication.ui

import CharactersFragment
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapp.ui.episodes.EpisodesFragment
import com.example.myapp.ui.locations.LocationsFragment

class MainPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CharactersFragment()
            1 -> EpisodesFragment()
            2 -> LocationsFragment()
            else -> throw IllegalStateException("Unexpected $position")
        }
    }
}
