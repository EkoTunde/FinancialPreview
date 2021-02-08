package com.ekosoftware.financialpreview.ui.pending.tabs

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.core.BaseListAdapter
import com.ekosoftware.financialpreview.data.model.movement.MovementUI
import com.ekosoftware.financialpreview.databinding.BaseListFragmentBinding
import com.ekosoftware.financialpreview.databinding.ItemPendingMovementBinding
import com.ekosoftware.financialpreview.presentation.HomeViewModel
import com.ekosoftware.financialpreview.ui.pending.tabs.adapters.PendingMovementListAdapter

class PendingMovementsFragment(private val onItemClicked: ((v: View, id: String) -> Unit)?) :
    BaseTabbedListFragment<MovementUI, ItemPendingMovementBinding>() {

    private val viewModel by activityViewModels<HomeViewModel>()

    override val title: String get() = Strings.get(R.string.pending_movements)

    private val movementListAdapter = PendingMovementListAdapter { v: View, summary ->
        onItemClicked?.let { it(v, summary.id) }
    }

    override val listAdapter: BaseListAdapter<MovementUI, ItemPendingMovementBinding>
        get() = movementListAdapter

    override var editable: Boolean = arguments?.getBoolean("editable") ?: false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
    }

    override fun onQueryTextChanged(query: String) = viewModel.submitSearchQuery(query)

    override fun recyclerViewDividerOrientation(): Int = LinearLayout.VERTICAL

    override fun onRetry(binding: BaseListFragmentBinding) = setData()

    private fun setData() = viewModel.movements.fetchData {
        movementListAdapter.submitList(it)
        //hideFab()
    }
}

/*override fun onCreateToolbar(appBarLayout: AppBarLayout, toolbar: Toolbar) {
    // If true, defines whether this fragment is editable (navigated from SelectionFragment or some subclass)
    // AppBarLayout, Toolbar and Menu are inflated and visible to provide search functionality
    if (args.editable) {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        toolbar.inflateMenu(R.menu.only_search_menu)
        toolbar.title = Strings.get(R.string.pending_movements)
    }
    // If false, fragment is inflated in a tab at PendingFragment
    else super.onCreateToolbar(appBarLayout, toolbar)
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
            viewModel.submitSearchQuery(query)
            return false
        }

        override fun onQueryTextChange(newText: String): Boolean {
            viewModel.submitSearchQuery(newText)
            return false
        }
    }
}*/
