package com.ekosoftware.financialpreview.core

import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.viewbinding.ViewBinding
import com.ekosoftware.financialpreview.MainActivity
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Strings
import com.google.android.material.appbar.AppBarLayout


/**
 * Base parent class to be extended by simple [Fragment]s
 * that need to display a list of data, with loading, success
 * with data, success without data and failure UI responses.
 * Provides fully automatic UI responses to [LiveData] objects
 * that contain [Resource]s wrapping [T] data.
 * It uses [ViewBinding] features with [K] provided to bind [BaseListAdapter]
 * sublclass items to views.
 * Also supports failure and "Retry Policy".
 *
 */
abstract class BaseListSelectionFragment<T, K : ViewBinding> : BaseListFragment<T, K>() {

    protected abstract val title: String

    override fun onCreateToolbar(appBarLayout: AppBarLayout, toolbar: Toolbar) {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar.inflateMenu(R.menu.only_search_menu)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        toolbar.title = title
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.only_search_menu, menu)
        val searchView =
            SearchView((context as MainActivity).supportActionBar?.themedContext ?: context)
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
                onSubmitQuery(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                onSubmitQuery(newText)
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    open fun onEdit() {}

    open fun onSubmitQuery(queryText: String) {}

}