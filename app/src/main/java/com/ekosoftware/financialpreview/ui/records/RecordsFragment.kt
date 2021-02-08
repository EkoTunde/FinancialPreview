package com.ekosoftware.financialpreview.ui.records

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
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
import com.ekosoftware.financialpreview.data.model.record.RecordUIShort
import com.ekosoftware.financialpreview.databinding.BaseListFragmentBinding
import com.ekosoftware.financialpreview.databinding.ItemRecordBinding
import com.ekosoftware.financialpreview.presentation.AccountsAndRecordsViewModel
import com.ekosoftware.financialpreview.presentation.RecordsFilterOptions
import com.google.android.material.appbar.AppBarLayout

class RecordsFragment : BaseListFragment<RecordUIShort, ItemRecordBinding>() {

    private val args: RecordsFragmentArgs by navArgs()

    private val viewModel by activityViewModels<AccountsAndRecordsViewModel>()

    private val recordsAdapter = RecordsListAdapter { _, record ->
        val directions = RecordsFragmentDirections.actionRecordsToDetailsRecordFragment(record.id)
        findNavController().navigate(directions)
    }

    override val listAdapter: BaseListAdapter<RecordUIShort, ItemRecordBinding> get() = recordsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
    }

    override fun onCreateToolbar(appBarLayout: AppBarLayout, toolbar: Toolbar) {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar.inflateMenu(R.menu.only_search_menu)
        (requireActivity() as MainActivity).setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        toolbar.title = Strings.get(R.string.records)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.only_search_menu, menu)
        val searchView: SearchView = menu.findItem(R.id.menu_item_search).actionView as SearchView
        searchView.setOnQueryTextListener(queryListener)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private val queryListener by lazy {
        object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.recordSearch(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.recordSearch(newText)
                return false
            }
        }
    }

    override fun recyclerViewDividerOrientation(): Int = LinearLayout.VERTICAL

    override fun onRetry(binding: BaseListFragmentBinding) {
        super.onRetry(binding)
        setData()
    }

    private fun setFilter(phrase: String) {
        viewModel.setFilterOptions(RecordsFilterOptions())
    }

    private fun setData() = viewModel.records(args.accountId).fetchData {
        recordsAdapter.submitList(it)
    }
}