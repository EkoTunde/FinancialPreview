package com.ekosoftware.financialpreview.ui.accounts

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
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
import com.ekosoftware.financialpreview.data.model.account.Account
import com.ekosoftware.financialpreview.databinding.ItemAccountBinding
import com.ekosoftware.financialpreview.presentation.AccountsAndRecordsViewModel
import com.google.android.material.appbar.AppBarLayout


class AccountsFragment : BaseListFragment<Account, ItemAccountBinding>() {

    private val viewModel by activityViewModels<AccountsAndRecordsViewModel>()

    private val accountListAdapter = AccountsListAdapter { _, account ->
        val directions = AccountsFragmentDirections.actionAccountsFragmentToRecordsFragment(account.id)
        findNavController().navigate(directions)
    }

    override val listAdapter: BaseListAdapter<Account, ItemAccountBinding> get() = accountListAdapter

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
        toolbar.title = Strings.get(R.string.accounts)
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
                viewModel.accountSearch(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.accountSearch(newText)
                return false
            }
        }
    }

    override fun recyclerViewDividerOrientation(): Int = LinearLayout.VERTICAL

    private fun setData() = viewModel.accounts.fetchData {
        accountListAdapter.submitList(it)
    }
}