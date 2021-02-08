package com.ekosoftware.financialpreview.ui.selection

import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.widget.SearchView
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.ekosoftware.financialpreview.MainActivity
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.core.BaseListAdapter
import com.ekosoftware.financialpreview.core.BaseListFragment
import com.ekosoftware.financialpreview.databinding.BaseListFragmentBinding
import com.ekosoftware.financialpreview.databinding.ItemSelectionExtendedBinding
import com.ekosoftware.financialpreview.presentation.SelectionViewModel
import com.ekosoftware.financialpreview.presentation.SimpleDisplayableData
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectFragment : BaseListFragment<SimpleDisplayableData, ItemSelectionExtendedBinding>() {

    companion object {
        private const val TAG = "SelectFragment"
    }

    private val args: SelectFragmentArgs by navArgs()

    private val selectionViewModel by activityViewModels<SelectionViewModel>()

    private val adapter = SimpleDisplayableDataAdapter { _, item -> goBack(item) }

    override val listAdapter: BaseListAdapter<SimpleDisplayableData, ItemSelectionExtendedBinding> get() = adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Toast.makeText(requireContext(), "${args.type}", Toast.LENGTH_SHORT).show()
        setData()
    }

    override fun onCreateToolbar(appBarLayout: AppBarLayout, toolbar: Toolbar) {
        setHasOptionsMenu(true)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar.inflateMenu(R.menu.selection_menu)
        (requireActivity() as MainActivity).setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        toolbar.title = when (args.type) {
            SelectionViewModel.ACCOUNTS -> Strings.get(R.string.account)
            SelectionViewModel.BUDGETS -> Strings.get(R.string.budget)
            SelectionViewModel.CATEGORIES -> Strings.get(R.string.category)
            SelectionViewModel.CURRENCIES -> Strings.get(R.string.currency)
            SelectionViewModel.MOVEMENTS -> Strings.get(R.string.movement)
            SelectionViewModel.SETTLE_GROUPS, SelectionViewModel.SETTLE_GROUPS_TO_ADD_TO_MOVEMENTS -> Strings.get(R.string.settle_group)
            else -> throw IllegalArgumentException("Given argument ${args.type} isn't a valid type for toolbar in ${this.javaClass.name}")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.selection_menu, menu)
        val searchView: SearchView = menu.findItem(R.id.menu_item_search).actionView as SearchView
        searchView.setOnQueryTextListener(queryListener)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private val queryListener by lazy {
        object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                selectionViewModel.setSearchText(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                selectionViewModel.setSearchText(newText)
                return false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_edit -> {
                onEdit()
                true
            }
            R.id.menu_item_clear_selection -> {
                goBack(null)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setData() = selectionViewModel.get(args.type, args.genericId).fetchData { adapter.submitList(it) }

    override fun recyclerViewDividerOrientation(): Int = LinearLayout.VERTICAL

    override fun onRetry(binding: BaseListFragmentBinding) = setData()

    fun onEdit() {
        //findNavController().navigate(SelectAccountFragmentDirections.selectAccountToAccounts())
    }

    private fun goBack(item: SimpleDisplayableData?) {
        when (args.type) {
            SelectionViewModel.ACCOUNTS -> selectionViewModel.setAccountId(item?.id)
            SelectionViewModel.BUDGETS -> selectionViewModel.setBudgetId(item?.id)
            SelectionViewModel.CATEGORIES -> selectionViewModel.setCategoryId(item?.id)
            SelectionViewModel.CURRENCIES -> selectionViewModel.setCurrencyId(item?.id)
            SelectionViewModel.MOVEMENTS -> selectionViewModel.setMovementId(item?.id)
            SelectionViewModel.SETTLE_GROUPS, SelectionViewModel.SETTLE_GROUPS_TO_ADD_TO_MOVEMENTS -> selectionViewModel.setSettleGroupId(item?.id)
            else -> throw IllegalArgumentException("Given argument ${args.type} isn't a valid type to deploy an action in ${this.javaClass.name}")
        }
        findNavController().navigateUp()
    }
}