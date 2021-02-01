package com.ekosoftware.financialpreview.ui.pending.tabs.adapters

import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements
import com.ekosoftware.financialpreview.ui.pending.tabs.PendingBudgetsFragment
import com.ekosoftware.financialpreview.ui.pending.tabs.PendingMovementsFragment
import com.ekosoftware.financialpreview.ui.pending.tabs.PendingSettleGroupsFragment
import java.lang.IllegalArgumentException

class PendingPagerAdapter(
    fragment: FragmentActivity,
    private val onItemClicked: ((v: View, id: String, position: Int, settleGroup: SettleGroupWithMovements?) -> Unit)?
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            0 -> PendingMovementsFragment { v, id -> onItemClicked?.let { it(v, id, position, null) } }
            1 -> PendingBudgetsFragment { v, id -> onItemClicked?.let { it(v, id, position, null) } }
            2 -> PendingSettleGroupsFragment { v, settleGroupWithMovements ->
                onItemClicked?.let {
                    it(v, "", position, settleGroupWithMovements)
                }
            }
            else -> throw IllegalArgumentException("${this.javaClass.name}: position doesn't exists")
        }
        fragment.arguments = bundleOf("editable" to false)
        return fragment
    }
}