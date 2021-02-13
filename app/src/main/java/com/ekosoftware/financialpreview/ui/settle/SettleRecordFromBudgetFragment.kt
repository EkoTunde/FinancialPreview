package com.ekosoftware.financialpreview.ui.settle

import android.util.Log
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ekosoftware.financialpreview.MainNavGraphDirections
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.data.model.budget.Budget
import com.ekosoftware.financialpreview.presentation.SelectionViewModel
import com.ekosoftware.financialpreview.util.forCommunicationAmount
import com.ekosoftware.financialpreview.util.forDisplayAmount
import com.ekosoftware.financialpreview.util.toast
import com.google.android.material.snackbar.Snackbar
import java.math.BigDecimal

class SettleRecordFromBudgetFragment : BaseSettleFragment<Budget>() {

    private val args: SettleRecordFromBudgetFragmentArgs by navArgs()

    private val selectionViewModel: SelectionViewModel by activityViewModels()

    private var isValidAmount = false

    override val type: Int get() = args.type

    override fun fetchData(): LiveData<Budget> = settleViewModel.getBudget(args.id)

    override fun setData(obj: Budget) {
            toast("agaian")
            with(binding) {
                currency.text = obj.currencyCode
                //amountToSettle.setText(obj.leftAmount.forDisplayAmount(obj.currencyCode))
                settleViewModel.setSettleAmount(obj.leftAmount.forCommunicationAmount())
                leftAmount.text = Strings.get(R.string.left_amount_budget_placeholder, obj.leftAmount.forDisplayAmount(obj.currencyCode), obj.name)
                btnAccount.text = Strings.get(R.string.select_account)
                btnAccount.setOnClickListener {
                    val action = MainNavGraphDirections.actionGlobalSelectFragment(SelectionViewModel.ACCOUNTS)
                    findNavController().navigate(action)
                }
                amountToSettle.doAfterTextChanged { editable ->
                    try {
                        editable?.toString()?.takeIf { it.isNotEmpty() }?.let { text ->
                            if (obj.leftAmount.forCommunicationAmount() < 0L && BigDecimal(text) < BigDecimal(obj.leftAmount.forDisplayAmount(obj.currencyCode))) {
                                amountToSettle.error = Strings.get(R.string.incorrect_negative_amount)
                                isValidAmount = false
                            } else if (obj.leftAmount.forCommunicationAmount() >= 0L && BigDecimal(text) > BigDecimal(
                                    obj.leftAmount.forDisplayAmount(
                                        obj.currencyCode
                                    )
                                )
                            ) {
                                amountToSettle.error = Strings.get(R.string.incorrect_positive_amount)
                                isValidAmount = false
                            } else if (obj.leftAmount.forCommunicationAmount() >= 0L && BigDecimal(text) > BigDecimal(
                                    obj.leftAmount.forDisplayAmount(
                                        obj.currencyCode
                                    )
                                )
                            ) {
                                amountToSettle.error = Strings.get(R.string.incorrect_positive_amount)
                                isValidAmount = false
                            } else {
                                isValidAmount = true
                                //settleViewModel.setSettleAmount(text.toDouble())
                            }
                        }
                    } catch (e: Exception) {
                    }
                }
                description.setHint(R.string.name)
                settleViewModel.setCurrency(obj.currencyCode)
                btnResetAmount.setOnClickListener {
                    amountToSettle.setText(obj.leftAmount.forDisplayAmount(obj.currencyCode))
                }
                btnOpenCalculator.setOnClickListener {
                    val currentText = when (val txt = amountToSettle.text.toString()) {
                        "", "-", "-0", "0" -> "0"
                        else -> if (BigDecimal(txt).toDouble() == BigDecimal("0.0").toDouble()) "0" else txt
                    }
                    val action = MainNavGraphDirections.actionGlobalCalculatorFragment4(currentText)
                    findNavController().navigate(action)
                }

                arrayOf(category, category.editText).forEach {
                    it?.setOnClickListener {
                        val action = MainNavGraphDirections.actionGlobalSelectFragment(SelectionViewModel.CATEGORIES)
                        findNavController().navigate(action)
                    }
                }
                category.isEndIconCheckable = false
            }
    }

    override fun onSave() {
        settleViewModel.settleRecordFromBudget(binding.description.editText?.text, binding.amountToSettle.text)
        findNavController().navigate(MainNavGraphDirections.actionGlobalPendingPageFragment())
    }

    override fun isRequiredInfoFulfilled(): Boolean {
        binding.amountToSettle.text.toString().let {
            toast(BigDecimal(it).toDouble().toString(), Toast.LENGTH_LONG)
            if (it.isEmpty() || it.isBlank() || it in arrayOf("-", "", "-0", "0") || BigDecimal(it).toDouble() == BigDecimal("0.0").toDouble()) {
                toast(BigDecimal(it).toString(), Toast.LENGTH_LONG)
                Snackbar.make(binding.root, R.string.insert_a_valid_amount, Snackbar.LENGTH_LONG).show()
                return false
            }
        }
        if (settleViewModel.getAccountId() == null) {
            Snackbar.make(binding.root, R.string.an_account_is_necessary, Snackbar.LENGTH_LONG).show()
            return false
        }
        if (settleViewModel.getCategoryId() == null) {
            Snackbar.make(binding.root, R.string.a_category_is_necessary, Snackbar.LENGTH_LONG).show()
            return false
        }
        return isValidAmount
    }

    override fun setSelectionResultsListener() {
        selectionViewModel.accountId.observe(viewLifecycleOwner) { it?.let { id -> settleViewModel.setAccountId(id) } }
        calculatorViewModel.getAmount().observe(viewLifecycleOwner) { amount ->
            amount?.let {
                settleViewModel.setSettleAmount(it)
            }
        }
        selectionViewModel.categoryId.observe(viewLifecycleOwner) { it?.let { id -> settleViewModel.setCategoryId(id) } }
    }

    override fun launchSelectionListener() {
        settleViewModel.getAccount().observe(viewLifecycleOwner) {
            it?.let {
                settleViewModel.setCurrency(it.currencyCode)
                binding.btnAccount.text = it.name
            }
        }
        settleViewModel.getCurrency().observe(viewLifecycleOwner) { it?.let { currency -> binding.currency.text = currency } }
        settleViewModel.getSettleAmount()
            .observe(viewLifecycleOwner) { leftAmount ->
                leftAmount?.let {
                    /*if (binding.amountToSettle.text.toString() != it.toString())*/ binding.amountToSettle.setText(it.toString())
                }
            }
        settleViewModel.getCategory().observe(viewLifecycleOwner) { it?.let { category -> binding.category.editText?.setText(category.name) } }
    }
}