package com.ekosoftware.financialpreview.presentation.ui.pending.tabs

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.lang.IllegalArgumentException

class PendingPagerAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PendingMovementsFragment()
            1 -> PendingSettleGroupsFragment()
            else -> throw IllegalArgumentException("${this.javaClass.name}: position doesn't exists")
        }
    }
}