package com.ofek.hunter.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ofek.hunter.fragments.InterviewListFragment

/**
 * ViewPager2 adapter for upcoming/past interview tabs.
 */
class InterviewsPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    // Return the right fragment based on tab position
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> InterviewListFragment.newInstance(InterviewListFragment.TYPE_UPCOMING)
            1 -> InterviewListFragment.newInstance(InterviewListFragment.TYPE_PAST)
            else -> InterviewListFragment.newInstance(InterviewListFragment.TYPE_UPCOMING)
        }
    }
}
