package com.ekosoftware.financialpreview.ui.records

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.ekosoftware.financialpreview.MainActivity
import com.ekosoftware.financialpreview.MainNavGraphDirections
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Constants
import com.ekosoftware.financialpreview.app.Constants.nan
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.core.BaseListAdapter
import com.ekosoftware.financialpreview.core.BaseListFragment
import com.ekosoftware.financialpreview.data.model.record.RecordUIShort
import com.ekosoftware.financialpreview.databinding.BaseListFragmentBinding
import com.ekosoftware.financialpreview.databinding.ItemRecordBinding
import com.ekosoftware.financialpreview.presentation.AccountsAndRecordsViewModel
import com.ekosoftware.financialpreview.presentation.RecordsFilterOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout

class RecordsFragment : BaseListFragment<RecordUIShort, ItemRecordBinding>() {

    private val args: RecordsFragmentArgs by navArgs()

    private val recordsViewModel by activityViewModels<AccountsAndRecordsViewModel>()

    private val recordsAdapter = RecordsListAdapter { _, record ->
        val directions = RecordsFragmentDirections.actionRecordsToDetailsRecordFragment(record.id)
        findNavController().navigate(directions)
    }

    override val listAdapter: BaseListAdapter<RecordUIShort, ItemRecordBinding> get() = recordsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun isFullScreen(): Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
    }

    override fun implementsFab(fab: FloatingActionButton): Boolean {
        fab.setImageResource(R.drawable.ic_done)
        return true
    }

    override fun onCreateToolbar(appBarLayout: AppBarLayout, toolbar: Toolbar) {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar.inflateMenu(R.menu.records_menu)
        (requireActivity() as MainActivity).setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        toolbar.title = Strings.get(R.string.records)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.records_menu, menu)
        val searchView: SearchView = menu.findItem(R.id.menu_item_search).actionView as SearchView
        searchView.setOnQueryTextListener(queryListener)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_filter -> toggleBottomSheetState()
            R.id.menu_item_edit -> {
                val action = RecordsFragmentDirections.actionGlobalEditAccountFragment(args.accountId)
                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val queryListener by lazy {
        object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                recordsViewModel.recordSearch(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                recordsViewModel.recordSearch(newText)
                return false
            }
        }
    }

    override fun recyclerViewDividerOrientation(): Int = LinearLayout.VERTICAL

    override fun onRetry(binding: BaseListFragmentBinding) {
        super.onRetry(binding)
        setData()
    }

    private fun setData() = recordsViewModel.records(args.accountId).fetchDataNoResource {
        recordsAdapter.submitList(it)
    }

    override fun implementsFilterBottomSheet(): Boolean = true
    override fun implementAmountRange(minLayout: TextInputLayout, maxLayout: TextInputLayout): Boolean {
        minLayout.editText?.doOnTextChanged { text, _, _, _ -> recordsViewModel.setMinValue(text) }
        maxLayout.editText?.doOnTextChanged { text, _, _, _ -> recordsViewModel.setMaxValue(text) }
        return true
    }

    override fun setOrderByAdapter(autoCompleteTextView: AutoCompleteTextView) {
        val items = resources.getStringArray(R.array.order_by_filter_list_for_records)
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        autoCompleteTextView.let {
            it.setAdapter(adapter)
            it.clearListSelection()
            it.setSelection(0)
            it.setOnItemClickListener { _, _, position, _ ->
                recordsViewModel.setRecordsOrderBy(position)
            }
        }
    }

    override fun onFabPressed() {
        val action = MainNavGraphDirections.actionGlobalSettleFragment(Constants.SETTLE_TYPE_SIMPLE_RECORD,nan, 0)
        findNavController().navigate(action)
    }

    override fun onDatesChangeListener(): ChipGroup.OnCheckedChangeListener {
        return ChipGroup.OnCheckedChangeListener { _, checkedId ->
            val days = when (checkedId) {
                R.id.a_week_chip -> 7
                R.id.a_month_chip -> 30
                R.id.three_month_chip -> 90
                R.id.half_year_chip -> 180
                R.id.a_year_chip -> 365
                else -> throw IllegalArgumentException("Provided check id ($checkedId) isn't a legal argument.")
            }
            recordsViewModel.setAmountOfDaysFilter(days)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recordsViewModel.clearSelectedData()
    }
}