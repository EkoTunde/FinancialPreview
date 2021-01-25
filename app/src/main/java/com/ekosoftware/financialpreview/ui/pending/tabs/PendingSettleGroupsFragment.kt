package com.ekosoftware.financialpreview.ui.pending.tabs

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ekosoftware.financialpreview.core.BaseListAdapter
import com.ekosoftware.financialpreview.core.BaseListFragment
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements
import com.ekosoftware.financialpreview.databinding.BaseListFragmentBinding
import com.ekosoftware.financialpreview.databinding.ItemPendingSettleGroupBinding
import com.ekosoftware.financialpreview.presentation.MainViewModel
import com.ekosoftware.financialpreview.ui.pending.tabs.adapters.PendingSettleGroupsListAdapter

class PendingSettleGroupsFragment :
    BaseListFragment<SettleGroupWithMovements, ItemPendingSettleGroupBinding>() {

    private val mainViewModel by activityViewModels<MainViewModel>()

    private val settleGroupAdapter = PendingSettleGroupsListAdapter {
        val directions = PendingSettleGroupsFragmentDirections.settleGroupsToEditSettleGroup(it)
        findNavController().navigate(directions)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
    }

    override val listAdapter: BaseListAdapter<SettleGroupWithMovements, ItemPendingSettleGroupBinding>
        get() = settleGroupAdapter

    override fun recyclerViewDividerOrientation(): Int = LinearLayout.HORIZONTAL

    override fun onRetry(binding: BaseListFragmentBinding) = setData()

    private fun setData() = mainViewModel.settleGroups.fetchData {
        settleGroupAdapter.submitList(it)
    }
}