package com.example.rickandmortyapplication.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapp.ui.episodes.EpisodeFragment
import com.example.myapp.ui.locations.LocationFragment

class MainPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CharactersFragment()
            1 -> EpisodeFragment()
            2 -> LocationFragment()
            else -> throw IllegalStateException("Unexpected $position")
        }
    }
}
