package com.ekosoftware.financialpreview.ui.entry

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ekosoftware.financialpreview.MainNavGraphDirections
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Constants
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.core.BaseEntryFragment
import com.ekosoftware.financialpreview.data.model.budget.Budget
import com.ekosoftware.financialpreview.databinding.EntryFragmentBudgetBinding
import com.ekosoftware.financialpreview.presentation.*
import com.ekosoftware.financialpreview.ui.selection.SelectFragment
import com.ekosoftware.financialpreview.util.forDisplay
import com.ekosoftware.financialpreview.util.forDisplayAmount
import com.ekosoftware.financialpreview.util.show

class EntryBudgetFragment : BaseEntryFragment() {

    private var _binding: EntryFragmentBudgetBinding? = null
    private val binding get() = _binding!!

    private val entryBudgetViewModel: EntryBudgetViewModel by activityViewModels()

    private val args: EntryBudgetFragmentArgs by navArgs()

    override val genericId: String get() = args.budgetId
    override val toolbar: Toolbar get() = binding.toolbar
    override val startViewForTransition: View get() = requireActivity().findViewById(R.id.main_fab)
    override val endViewForTransition: View get() = binding.entryBudgetCardView
    override val targetId: Int get() = R.id.entryBudgetCardView
    override val addTitle: String get() = Strings.get(R.string.add_budget)
    override val editTitle: String get() = Strings.get(R.string.title_edit_budget)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = EntryFragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (args.budgetId != Constants.nan) {
            binding.leftAmount.show()
            entryBudgetViewModel.get(args.budgetId).observe(viewLifecycleOwner) { budget ->
                budget?.let {
                    populateUI(it)
                    entryBudgetViewModel.setFrequency(it.frequency)
                    entryBudgetViewModel.budgetRetrievedOk()
                    entryBudgetViewModel.get(args.budgetId).removeObservers(viewLifecycleOwner)
                }
            }
        } else {
            binding.startingAmount.editText?.doOnTextChanged { text, _, _, _ ->
                binding.leftAmount.editText?.setText(text)
            }
        }

    }

    private fun populateUI(budget: Budget) {
        with(binding) {
            leftAmount.editText?.setText(budget.leftAmount.forDisplayAmount(budget.currencyCode))
            startingAmount.editText?.setText(budget.startingAmount.forDisplayAmount(budget.currencyCode))
            currency.editText?.setText(budget.currencyCode)
            frequency.editText?.setText(budget.frequency.forDisplay())
            name.editText?.setText(budget.name)
            description.editText?.setText(budget.description)
        }
    }

    override fun fetchSelectionData() {
        selectionViewModel.currencyId.observe(viewLifecycleOwner) { id -> entryBudgetViewModel.setCurrencyCode(id) }
        frequencyViewModel.getFrequency().observe(viewLifecycleOwner) { frequency -> frequency?.let { entryBudgetViewModel.setFrequency(it) } }
        calculatorViewModel.getAmount().observe(viewLifecycleOwner) { amount -> entryBudgetViewModel.setLeftAmount(amount) }
        with(entryBudgetViewModel) {
            currencyCode().observe(viewLifecycleOwner) { code -> binding.currency.editText?.setText(code) }
            frequency().observe(viewLifecycleOwner) { frequency -> binding.frequency.editText?.setText(frequency.forDisplay()) }
            leftAmount().observe(viewLifecycleOwner) { leftAmount ->
                leftAmount?.let {
                    val asText = startAmountToString(it)
                    binding.startingAmount.editText?.setText(asText)
                }
            }
        }
    }

    /**
     * Sets listeners for editable text views that trigger navigation to [SelectFragment].
     */
    override fun setSelectionListeners() {
        with(binding) {
            currency.setNavigation(MainNavGraphDirections.actionGlobalSelectFragment(SelectionViewModel.CURRENCIES))
            frequency.setNavigation(EntryBudgetFragmentDirections.actionEditBudgetFragmentToEditFrequencyFragment4(entryBudgetViewModel.getSingleFrequency()))
            startingAmount.setEndIconOnClickListener {
                val currentText = binding.startingAmount.editText?.text.toString()
                val action = MainNavGraphDirections.actionGlobalCalculatorFragment4(currentText)
                findNavController().navigate(action)
            }
        }
    }

    /**
     * Listens for text changed and clears error message
     * if there was any and the error no longer is there.
     */
    override fun setCleanErrorOnChangedText() {
        with(binding) {
            arrayOf(startingAmount, leftAmount, currency, name, frequency).forEach {
                it.editText?.doOnTextChanged { text, _, _, _ ->
                    if (text.toString().isNotEmpty()) {
                        it.error = null
                    }
                }
            }
        }
    }

    /**
     * Called when user is intended to save data. Checks if required info is
     * filled and performs saving action calling ViewModel.
     * Finally, navigates up.
     */
    override fun done(): Boolean {
        if (isRequiredInfoCompleted()) {
            val startAmount = binding.startingAmount.editText!!.text.toString().toDouble()
            //val id = if (args.movementUI != null) args.movementUI!!.id else Constants.nan
            entryBudgetViewModel.save(
                args.budgetId,
                if (args.budgetId == Constants.nan) startAmount else binding.leftAmount.editText?.text.toString().toDouble(),
                startAmount,
                binding.currency.editText!!.text.toString(),
                binding.name.editText!!.text.toString(),
                binding.description.editText!!.text.toString()
            )
            findNavController().navigateUp()
        }
        return super.done()
    }

    /**
     * Checks if required form data is filled, one by one.
     * If not, triggers an error.
     *
     * @return [Boolean] whether all info is filled.
     */
    private fun isRequiredInfoCompleted(): Boolean {
        binding.startingAmount.let {
            // Checks starting amount is filled
            if (it.editText?.text.toString().isEmpty()) {
                it.error = Strings.get(R.string.mandatory)
                return false
            }
        }
        binding.currency.let {
            if (it.editText?.text.toString().isEmpty()) {
                it.error = Strings.get(R.string.mandatory)
                return false
            }
        }
        binding.name.let {
            if (it.editText?.text.toString().isEmpty()) {
                it.error = Strings.get(R.string.mandatory)
                return false
            }
        }
        binding.frequency.let {
            if (it.editText?.text.toString().isEmpty()) {
                it.error = Strings.get(R.string.mandatory)
                return false
            }
        }
        return true
    }

    /**
     * Deletes the budget.
     */
    override fun delete(): Boolean {
        entryBudgetViewModel.delete(args.budgetId)
        findNavController().navigate(MainNavGraphDirections.actionGlobalPendingPageFragment())
        return super.delete()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        entryBudgetViewModel.clearAllSelectedData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}