package com.ekosoftware.financialpreview.ui.selection

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.core.BaseListAdapter
import com.ekosoftware.financialpreview.core.BaseListSelectionFragment
import com.ekosoftware.financialpreview.databinding.BaseListFragmentBinding
import com.ekosoftware.financialpreview.databinding.ItemSelectionExtendedBinding
import com.ekosoftware.financialpreview.presentation.*

class SelectFragment :
    BaseListSelectionFragment<SimpleDisplayedData, ItemSelectionExtendedBinding>() {

    //val args : SelectiFragmentArgs by navArgs()

    val type: Int = 0

    private val shareViewModel by activityViewModels<ShareViewModel>()

    private val selectionViewModel by activityViewModels<SelectionViewModel>()

    private val adapter = SimpleDisplayedDataItemAdapter<String> { _, item ->
        shareViewModel.selectAccountItem(item.id, item.name)
    }

    override val listAdapter: BaseListAdapter<SimpleDisplayedData, ItemSelectionExtendedBinding>
        get() = adapter
    override val title: String get() = Strings.get(R.string.account)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
    }

    private fun setData() {

    }

    override fun recyclerViewDividerOrientation(): Int = LinearLayout.VERTICAL

    override fun onRetry(binding: BaseListFragmentBinding) = setData()

    override fun onEdit() {
        //findNavController().navigate(SelectAccountFragmentDirections.selectAccountToAccounts())
    }

    //override fun onSubmitQuery(queryText: String) = selectionViewModel.accountSearch(queryText)
}