package com.ekosoftware.financialpreview.ui.pending.tabs

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ekosoftware.financialpreview.core.BaseListAdapter
import com.ekosoftware.financialpreview.core.BaseListFragment
import com.ekosoftware.financialpreview.data.model.movement.MovementSummary
import com.ekosoftware.financialpreview.databinding.BaseListFragmentBinding
import com.ekosoftware.financialpreview.databinding.ItemPendingMovementBinding
import com.ekosoftware.financialpreview.presentation.MainViewModel
import com.ekosoftware.financialpreview.ui.pending.tabs.adapters.PendingMovementListAdapter

class PendingMovementsFragment : BaseListFragment<MovementSummary, ItemPendingMovementBinding>() {

    private val mainViewModel by activityViewModels<MainViewModel>()

    private val movementListAdapter = PendingMovementListAdapter {
        val directions = PendingMovementsFragmentDirections.movementsToEditMovement(it.movementId)
        findNavController().navigate(directions)
    }

    override val listAdapter: BaseListAdapter<MovementSummary, ItemPendingMovementBinding>
        get() = movementListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
    }

    override fun recyclerViewDividerOrientation(): Int = LinearLayout.HORIZONTAL

    override fun onRetry(binding: BaseListFragmentBinding) = setData()

    private fun setData() = mainViewModel.movements.fetchData { movementListAdapter.submitList(it) }
}