package com.ekosoftware.financialpreview.presentation.ui.pending.tabs.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ekosoftware.financialpreview.presentation.ui.pending.tabs.PendingBudgetsFragment
import com.ekosoftware.financialpreview.presentation.ui.pending.tabs.PendingMovementsFragment
import com.ekosoftware.financialpreview.presentation.ui.pending.tabs.PendingSettleGroupsFragment
import java.lang.IllegalArgumentException

class PendingPagerAdapter(fragment: FragmentActivity, private val interaction: Interaction?) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        interaction?.onTabSelected(position)
        return when (position) {
            0 -> PendingMovementsFragment()
            1 -> PendingBudgetsFragment()
            2 -> PendingSettleGroupsFragment()
            else -> throw IllegalArgumentException("${this.javaClass.name}: position doesn't exists")
        }
    }

    interface Interaction {
        fun onTabSelected(position: Int)
    }

}