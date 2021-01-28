package com.ekosoftware.financialpreview.ui.categories

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.ekosoftware.financialpreview.MainActivity
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.core.BaseListAdapter
import com.ekosoftware.financialpreview.core.BaseListFragment
import com.ekosoftware.financialpreview.data.model.Category
import com.ekosoftware.financialpreview.databinding.ItemSelectionExtendedBinding
import com.ekosoftware.financialpreview.presentation.AccountsAndRecordsViewModel
import com.ekosoftware.financialpreview.presentation.CategoriesViewModel
import com.ekosoftware.financialpreview.ui.accounts.AccountsFragmentDirections
import com.google.android.material.appbar.AppBarLayout

class CategoriesFragment : BaseListFragment<Category, ItemSelectionExtendedBinding>() {

    private val viewModel by activityViewModels<CategoriesViewModel>()

    private val categoriesListAdapter = CategoriesListAdapter { _, _ ->
        //val directions = CategoriesFragmentDir
        //findNavController().navigate(directions)
    }

    override val listAdapter: BaseListAdapter<Category, ItemSelectionExtendedBinding>
        get() = categoriesListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
    }

    override fun onCreateToolbar(appBarLayout: AppBarLayout, toolbar: Toolbar) {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        toolbar.inflateMenu(R.menu.only_search_menu)
        toolbar.title = Strings.get(R.string.accounts)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.only_search_menu, menu)
        val searchView = SearchView(
            (requireContext() as MainActivity).supportActionBar?.themedContext ?: requireContext()
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
                //viewModel.accountSearch(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                //viewModel.accountSearch(newText)
                return false
            }
        }
    }

    override fun recyclerViewDividerOrientation(): Int = LinearLayout.VERTICAL

    private fun setData() {
        viewModel.categories.observe(viewLifecycleOwner, {

        })
    }
}