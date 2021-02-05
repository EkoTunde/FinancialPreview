package com.ekosoftware.financialpreview.ui.pending.tabs

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.core.BaseListAdapter
import com.ekosoftware.financialpreview.data.model.budget.Budget
import com.ekosoftware.financialpreview.databinding.BaseListFragmentBinding
import com.ekosoftware.financialpreview.databinding.ItemPendingBudgetBinding
import com.ekosoftware.financialpreview.presentation.HomeViewModel
import com.ekosoftware.financialpreview.ui.pending.tabs.adapters.PendingBudgetsListAdapter


class PendingBudgetsFragment(private val onItemClicked: ((v: View, id: String) -> Unit)?) :
    BaseTabbedListFragment<Budget, ItemPendingBudgetBinding>() {

    private val viewModel by activityViewModels<HomeViewModel>()

    override var editable: Boolean = arguments?.getBoolean("editable") ?: false
    override val title: String get() = Strings.get(R.string.pending_budgets)

    private val pendingBudgetsListAdapter = PendingBudgetsListAdapter { v, budget -> onItemClicked?.let { it(v, budget.id) } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
    }

    override fun onQueryTextChanged(query: String) = viewModel.submitSearchQuery(query)

    override val listAdapter: BaseListAdapter<Budget, ItemPendingBudgetBinding>
        get() = pendingBudgetsListAdapter

    override fun onRetry(binding: BaseListFragmentBinding) = setData()

    private fun setData() = viewModel.budgets.fetchData {
        pendingBudgetsListAdapter.submitList(it)
        hideFab()
    }
}