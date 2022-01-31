package com.example.rez.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.rez.ui.fragment.dashboard.AboutFragment
import com.example.rez.ui.fragment.dashboard.ReviewFragment

class TableDetailsFragmentViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = NUM_PAGES

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> ReviewFragment()
            else -> AboutFragment()
        }
    }

    companion object {
        private const val NUM_PAGES = 2
    }
}
