package com.ekosoftware.financialpreview.ui.pending.tabs

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.core.BaseListAdapter
import com.ekosoftware.financialpreview.core.Resource
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements
import com.ekosoftware.financialpreview.databinding.BaseListFragmentBinding
import com.ekosoftware.financialpreview.databinding.ItemPendingSettleGroupBinding
import com.ekosoftware.financialpreview.presentation.MainViewModel
import com.ekosoftware.financialpreview.ui.pending.PendingFragmentDirections
import com.ekosoftware.financialpreview.ui.pending.tabs.adapters.PendingSettleGroupsListAdapter

class PendingSettleGroupsFragment(private val onItemClicked: ((v: View, settleGroupWithMovements: SettleGroupWithMovements) -> Unit)?) :
    BaseTabbedListFragment<SettleGroupWithMovements, ItemPendingSettleGroupBinding>() {

    private val viewModel by activityViewModels<MainViewModel>()

    override var editable: Boolean = arguments?.getBoolean("editable") ?: false
    override val title: String get() = Strings.get(R.string.settle_groups)

    private val settleGroupAdapter = PendingSettleGroupsListAdapter { v, group -> onItemClicked?.let { it(v, group) } }

    override val listAdapter: BaseListAdapter<SettleGroupWithMovements, ItemPendingSettleGroupBinding>
        get() = settleGroupAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
    }

    override fun onQueryTextChanged(query: String) = viewModel.submitSearchQuery(query)

    override fun onRetry(binding: BaseListFragmentBinding) = setData()

    private fun setData() = viewModel.homeData.observe(viewLifecycleOwner) { result ->
        when (result) {
            is Resource.Loading -> {
                loading()
            }
            is Resource.Success -> {
                success()
                settleGroupAdapter.submitList(result.data.settleGroupsWithMovements)
            }
            is Resource.Failure -> {
                failure()
            }
        }
        hideFab()
    }
}