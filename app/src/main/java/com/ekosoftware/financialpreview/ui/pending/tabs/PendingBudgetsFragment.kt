package com.ekosoftware.financialpreview.ui.pending.tabs

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ekosoftware.financialpreview.core.BaseListAdapter
import com.ekosoftware.financialpreview.core.BaseListFragment
import com.ekosoftware.financialpreview.data.model.budget.Budget
import com.ekosoftware.financialpreview.databinding.BaseListFragmentBinding
import com.ekosoftware.financialpreview.databinding.ItemPendingBudgetBinding
import com.ekosoftware.financialpreview.presentation.MainViewModel
import com.ekosoftware.financialpreview.ui.pending.tabs.adapters.PendingBudgetsListAdapter


class PendingBudgetsFragment : BaseListFragment<Budget, ItemPendingBudgetBinding>() {

    private val mainViewModel by activityViewModels<MainViewModel>()

    private val pendingBudgetsListAdapter = PendingBudgetsListAdapter {
        val directions = PendingBudgetsFragmentDirections.budgetsToEditBudget(it.id)
        findNavController().navigate(directions)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
    }

    override val listAdapter: BaseListAdapter<Budget, ItemPendingBudgetBinding>
        get() = pendingBudgetsListAdapter

    override fun recyclerViewDividerOrientation(): Int = LinearLayout.HORIZONTAL

    override fun onRetry(binding: BaseListFragmentBinding) = setData()

    private fun setData() = mainViewModel.budgets.fetchData {
        pendingBudgetsListAdapter.submitList(it)
    }
}