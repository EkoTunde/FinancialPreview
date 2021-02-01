package com.ekosoftware.financialpreview.ui.records

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.ekosoftware.financialpreview.MainActivity
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.core.BaseListAdapter
import com.ekosoftware.financialpreview.core.BaseListFragment
import com.ekosoftware.financialpreview.data.model.record.RecordSummary
import com.ekosoftware.financialpreview.databinding.BaseListFragmentBinding
import com.ekosoftware.financialpreview.databinding.ItemRecordBinding
import com.ekosoftware.financialpreview.presentation.AccountsAndRecordsViewModel
import com.ekosoftware.financialpreview.presentation.RecordsFilterOptions
import com.google.android.material.appbar.AppBarLayout

class RecordsFragment : BaseListFragment<RecordSummary, ItemRecordBinding>() {

    private val viewModel by activityViewModels<AccountsAndRecordsViewModel>()

    private val recordsAdapter = RecordsListAdapter { _, record ->
        val directions = RecordsFragmentDirections.actionRecordsToDetailsRecordFragment(record)
        findNavController().navigate(directions)
    }

    override val listAdapter: BaseListAdapter<RecordSummary, ItemRecordBinding>
        get() = recordsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
    }

    override fun onCreateToolbar(appBarLayout: AppBarLayout, toolbar: Toolbar) {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        toolbar.inflateMenu(R.menu.only_search_menu)
        toolbar.title = Strings.get(R.string.records)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.only_search_menu, menu)
        val searchView =
            SearchView(
                (requireContext() as MainActivity).supportActionBar?.themedContext
                    ?: requireContext()
            )
        menu.findItem(R.id.menu_item_search).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
            actionView = searchView
        }

        searchView.setOnQueryTextListener(queryListener)
        searchView.setOnClickListener { view ->
            Toast.makeText(
                requireContext(),
                "hola",
                Toast.LENGTH_SHORT
            ).show()
        }
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

    private fun setData() = viewModel.records.fetchData {
        recordsAdapter.submitList(it)
    }
}