package com.ekosoftware.financialpreview.ui.settle

import android.os.Bundle
import android.view.*
import androidx.annotation.MenuRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.ekosoftware.financialpreview.MainActivity
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Constants
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.databinding.FragmentSettleBinding
import com.ekosoftware.financialpreview.presentation.CalculatorViewModel
import com.ekosoftware.financialpreview.presentation.SettleViewModel
import com.ekosoftware.financialpreview.util.visible

/*
private val onDataReceived: (data: T) -> Unit
 */

abstract class BaseSettleFragment<T>() : Fragment() {
    private var _binding: FragmentSettleBinding? = null
    protected val binding get() = _binding!!

    protected abstract val type: Int

    protected val settleViewModel: SettleViewModel by activityViewModels()
    protected val calculatorViewModel: CalculatorViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettleBinding.inflate(inflater, container, false)
        setUpToolbar()
        return binding.root
    }

    private fun setUpToolbar() {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        (requireActivity() as MainActivity).setSupportActionBar(binding.toolbar)
        binding.toolbar.inflateMenu(R.menu.settle_menu)
        binding.toolbar.title = when (type) {
            Constants.SETTLE_TYPE_MOVEMENT -> Strings.get(R.string.settle_movement)
            else -> "hola"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.settle_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_save -> {
                onSave()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toggleUIElementsForSettleType()
        fetchData()?.let { it.observe(viewLifecycleOwner) { obj -> setData(obj) } }
        setSelectionResultsListener()
        launchSelectionListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        settleViewModel.clearData()
    }

    private fun toggleUIElementsForSettleType() = when (type) {
        Constants.SETTLE_TYPE_MOVEMENT -> toggleVisibility(dataSummary = true, account = true)
        Constants.SETTLE_TYPE_SIMPLE_RECORD -> toggleVisibility(account = true, description = true, category = true, budget = true)
        // DataSummary is needed to indicate this is a transfer in the title
        Constants.SETTLE_TYPE_TRANSFER -> toggleVisibility(dataSummary = true, transferAccountTable = true, description = true)
        Constants.SETTLE_TYPE_LOAN_DEBT -> toggleVisibility(account = true, loanDebt = true)
        Constants.SETTLE_TYPE_BUDGET_RECORD -> toggleVisibility(account = true, description = true, category = true, budget = true)
        else -> throw IllegalArgumentException("Provided $type type is not valid type. ")
    }

    private fun toggleVisibility(
        dataSummary: Boolean = false,
        transferAccountTable: Boolean = false,
        account: Boolean = false,
        description: Boolean = false,
        category: Boolean = false,
        loanDebt: Boolean = false,
        budget: Boolean = false
    ) = with(binding) {
        movementDataSummary.visible(dataSummary)
        transferAccountTableLayout.visible(transferAccountTable)
        btnAccount.visible(account)
        this.description.visible(description)
        this.category.visible(category)
        checkBoxGenerateProportionateMovement.visible(loanDebt)
        debtorLenderName.visible(loanDebt)
        loanDebtRadioGroup.visible(loanDebt)
        this.budget.visible(budget)

    }

    open fun fetchData(): LiveData<T>? = null

    open fun setData(obj: T) {}

    open fun onSave() {}

    open fun setSelectionResultsListener() {}

    open fun launchSelectionListener() {}
}