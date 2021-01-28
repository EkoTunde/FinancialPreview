package com.ekosoftware.financialpreview.ui.pending.tabs

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.viewbinding.ViewBinding
import com.ekosoftware.financialpreview.MainActivity
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.core.BaseListFragment
import com.google.android.material.appbar.AppBarLayout

abstract class BaseTabbedListFragment<T, K : ViewBinding> : BaseListFragment<T, K>() {

    protected abstract val editable: Boolean
    protected abstract val title: String

    /**
     * Makes toolbar visible.
     */
    override fun onCreateToolbar(appBarLayout: AppBarLayout, toolbar: Toolbar) {
        // If true, defines whether this fragment is editable (navigated from SelectionFragment or some subclass)
        // AppBarLayout, Toolbar and Menu are inflated and visible to provide search functionality
        if (editable) {
            val navController = findNavController()
            val appBarConfiguration = AppBarConfiguration(navController.graph)
            toolbar.setupWithNavController(navController, appBarConfiguration)
            toolbar.inflateMenu(R.menu.only_search_menu)
            toolbar.title = title
        }
        // If false, fragment is inflated in a tab at PendingFragment
        else super.onCreateToolbar(appBarLayout, toolbar)
    }

    /**
     * Inflates search menu and adds search functionality
     */
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

    /**
     * Called when [SearchView]'s text in [Toolbar]'s [Menu] is changed.
     */
    open fun onQueryTextChanged(query: String) {}

    private val queryListener by lazy {
        object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                onQueryTextChanged(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                onQueryTextChanged(newText)
                return false
            }
        }
    }
}