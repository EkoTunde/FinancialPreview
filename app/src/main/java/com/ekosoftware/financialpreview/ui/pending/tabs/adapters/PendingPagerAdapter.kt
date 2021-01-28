package com.ekosoftware.financialpreview.ui.pending.tabs.adapters

import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ekosoftware.financialpreview.ui.pending.tabs.PendingBudgetsFragment
import com.ekosoftware.financialpreview.ui.pending.tabs.PendingMovementsFragment
import com.ekosoftware.financialpreview.ui.pending.tabs.PendingSettleGroupsFragment
import java.lang.IllegalArgumentException

class PendingPagerAdapter(
    fragment: FragmentActivity,
    private val interaction: Interaction?,
    private val onItemClicked: ((v: View, id: String) -> Unit)?
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        interaction?.onTabSelected(position)
        val fragment = when (position) {
            0 -> PendingMovementsFragment() { v, id ->
                onItemClicked?.let { it(v, id) }
            }
            1 -> PendingBudgetsFragment()
            2 -> PendingSettleGroupsFragment()
            else -> throw IllegalArgumentException("${this.javaClass.name}: position doesn't exists")
        }
        fragment.arguments = bundleOf("editable" to false)
        return fragment
    }


    interface Interaction {
        fun onTabSelected(position: Int)
    }

}