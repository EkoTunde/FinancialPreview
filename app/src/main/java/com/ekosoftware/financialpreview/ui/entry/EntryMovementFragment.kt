package com.ekosoftware.financialpreview.ui.entry

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AutoCompleteTextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Slide
import com.ekosoftware.financialpreview.MainActivity
import com.ekosoftware.financialpreview.MainNavGraphDirections
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Constants
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.data.model.movement.Movement
import com.ekosoftware.financialpreview.databinding.EntryFragmentMovementBinding
import com.ekosoftware.financialpreview.presentation.CalculatorViewModel
import com.ekosoftware.financialpreview.presentation.EntryMovementViewModel
import com.ekosoftware.financialpreview.presentation.FrequencyViewModel
import com.ekosoftware.financialpreview.presentation.SelectionViewModel
import com.ekosoftware.financialpreview.ui.selection.SelectFragment
import com.ekosoftware.financialpreview.util.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal

@AndroidEntryPoint
class EntryMovementFragment : Fragment() {

    companion object {
        private const val TAG = "EntryMovementFragment"
    }

    private var _binding: EntryFragmentMovementBinding? = null
    private val binding get() = _binding!!

    private val entryMovementViewModel: EntryMovementViewModel by activityViewModels()
    private val selectionViewModel: SelectionViewModel by activityViewModels()
    private val frequencyViewModel: FrequencyViewModel by activityViewModels()
    private val calculatorViewModel: CalculatorViewModel by activityViewModels()

    private val args: EntryMovementFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = EntryFragmentMovementBinding.inflate(inflater, container, false)
        setUpToolbar()
        return binding.root
    }

    private fun setUpToolbar() {
        binding.toolbar.inflateMenu(
            if (args.movementId == Constants.nan) {
                R.menu.save_menu
            } else R.menu.save_delete_menu
        )
        (requireActivity() as MainActivity).setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.toolbar.title = if (args.movementId == Constants.nan) {
            Strings.get(R.string.title_add_movement)
        } else {
            Strings.get(R.string.title_edit_movement)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(
            if (args.movementId == Constants.nan) {
                R.menu.save_menu
            } else R.menu.save_delete_menu, menu
        )
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Handles saving button onClickListener
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_save -> {
                done()
                true
            }
            R.id.menu_item_delete -> {
                delete()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTransition() // Run Animation

        if (args.movementId != Constants.nan) {
            binding.leftAmount.show()
            entryMovementViewModel.movement(args.movementId).observe(viewLifecycleOwner) { movement ->
                movement?.let {
                    populateUI(it)
                    entryMovementViewModel.setFrequency(it.frequency)
                    entryMovementViewModel.movementRetrievedOk()
                    entryMovementViewModel.movement(args.movementId).removeObservers(viewLifecycleOwner)
                }
            }
            binding.leftAmount.setEndIconOnClickListener { warnLeftAmountChanging() }
        } else {
            binding.startingAmount.editText?.doOnTextChanged { text, _, _, _ -> binding.leftAmount.editText?.setText(text) }
        }
        fetchSelectionData()
        setSelectionListeners()
        setCleanErrorOnChangedText()
    }

    /**
     * Set starting and ending transition for this fragment.
     */
    private fun setTransition() {
        enterTransition = MaterialContainerTransform().apply {
            startView = requireActivity().findViewById(R.id.main_fab)/*startingView*/
            endView = binding.entryMovementCardView
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            scrimColor = Color.TRANSPARENT
            containerColor = requireContext().themeColor(R.attr.colorSurface)
            startContainerColor = requireContext().themeColor(R.attr.colorSecondary)
            endContainerColor = requireContext().themeColor(R.attr.colorSurface)
        }
        returnTransition = Slide().apply {
            duration = resources.getInteger(R.integer.motion_duration_medium).toLong()
            addTarget(R.id.entryMovementCardView)
        }
    }

    /**
     * Run observers meant to handle data which is chosen in [SelectFragment]
     */
    private fun fetchSelectionData() {
        selectionViewModel.accountId.observe(viewLifecycleOwner) { accountId ->
            binding.currency.isEnabled = accountId == null
            entryMovementViewModel.setAccountId(accountId)
        }
        selectionViewModel.budgetId.observe(viewLifecycleOwner) { id -> entryMovementViewModel.setBudgetId(id) }
        selectionViewModel.categoryId.observe(viewLifecycleOwner) { id -> entryMovementViewModel.setCategoryId(id) }
        selectionViewModel.currencyId.observe(viewLifecycleOwner) { id -> entryMovementViewModel.setCurrencyCode(id) }
        frequencyViewModel.getFrequency().observe(viewLifecycleOwner) { frequency -> frequency?.let { entryMovementViewModel.setFrequency(it) } }
        calculatorViewModel.getAmount().observe(viewLifecycleOwner) { amount -> entryMovementViewModel.setLeftAmount(amount) }
        with(entryMovementViewModel) {
            accountName().observe(viewLifecycleOwner) { name -> binding.account.editText?.setText(name) }
            budgetName().observe(viewLifecycleOwner) { name -> binding.budget.editText?.setText(name) }
            categoryName().observe(viewLifecycleOwner) { name -> binding.category.editText?.setText(name) }
            currencyCode().observe(viewLifecycleOwner) { code -> binding.currency.editText?.setText(code) }
            frequency().observe(viewLifecycleOwner) { frequency -> binding.frequency.editText?.setText(frequency.forDisplay()) }
            getLeftAmount().observe(viewLifecycleOwner) { leftAmount ->
                leftAmount?.let {
                    val asText = startAmountToString(it)
                    binding.startingAmount.editText?.setText(asText) }
            }
        }
    }

    /**
     * Takes out fraction part of a double if there is any and is based on
     * decimal point and zeros.
     *
     * @param amount representing an amount in [Double]
     *
     * @return [String] without decimal points and zeros if there is any.
     *
     */
    private fun startAmountToString(amount: Double): String {
        return if (BigDecimal(amount).hasUselessDecimals()) {
            amount.toString().substring(0, amount.toString().indexOf("."))
        } else {
            amount.toString()
        }
    }

    /**
     * Populates editable text views with [Movement]'s data.
     */
    private fun populateUI(movement: Movement) {
        with(binding) {
            leftAmount.editText?.setText(movement.leftAmount.forDisplayAmount(movement.currencyCode))
            startingAmount.editText?.setText(movement.startingAmount.forDisplayAmount(movement.currencyCode))
            currency.editText?.setText(movement.currencyCode)
            frequency.editText?.setText(movement.frequency?.forDisplay())
            name.editText?.setText(movement.name)
            description.editText?.setText(movement.description)
            movement.accountId?.let {
                entryMovementViewModel.setAccountId(it)
                currency.isEnabled = false
            }
            movement.categoryId?.let { entryMovementViewModel.setCategoryId(it) }
            movement.budgetId?.let { entryMovementViewModel.setBudgetId(it) }
        }
    }

    /**
     * Sets listeners for editable text views that trigger navigation to [SelectFragment].
     */
    private fun setSelectionListeners() {
        with(binding) {
            account.setNavigation(MainNavGraphDirections.actionGlobalSelectFragment(SelectionViewModel.ACCOUNTS))
            budget.setNavigation(MainNavGraphDirections.actionGlobalSelectFragment(SelectionViewModel.BUDGETS))
            category.setNavigation(MainNavGraphDirections.actionGlobalSelectFragment(SelectionViewModel.CATEGORIES))
            currency.setNavigation(MainNavGraphDirections.actionGlobalSelectFragment(SelectionViewModel.CURRENCIES))
            frequency.setNavigation(EntryMovementFragmentDirections.actionEditMovementToEditFrequency(entryMovementViewModel.getSingleFrequency()))
            startingAmount.setEndIconOnClickListener {
                val currentText = binding.startingAmount.editText?.text.toString()
                val action = MainNavGraphDirections.actionGlobalCalculatorFragment4(currentText)
                findNavController().navigate(action)
            }
        }
    }

    /**
     * Listens for text changed and clears error message if there was any and the error no longer is there.
     */
    private fun setCleanErrorOnChangedText() {
        with(binding) {
            arrayOf(startingAmount, leftAmount, currency, category, name, frequency).forEach {
                it.editText?.doOnTextChanged { text, _, _, _ ->
                    if (text.toString().isNotEmpty()) {
                        it.error = null
                    }
                }
            }
        }
    }

    /**
     * Sets navigation action behaviour for layouts user may use to edit data.
     */
    private fun TextInputLayout.setNavigation(action: NavDirections) {
        (editText as? AutoCompleteTextView)?.let {
            isEndIconCheckable = false
        }
        setEndIconOnClickListener { findNavController().navigate(action) }
        arrayListOf(this, editText).forEach { it?.setOnClickListener { findNavController().navigate(action) } }
    }


    /**
     * Deletes the movement.
     */
    private fun delete() {
        entryMovementViewModel.delete(args.movementId)
        findNavController().navigate(MainNavGraphDirections.actionGlobalPendingPageFragment())
    }

    /**
     * Called when user is intended to save data. Checks if required info is
     * filled and performs saving action calling ViewModel.
     * Finally, navigates up.
     */
    private fun done() {
        if (isRequiredInfoCompleted()) {
            val startAmount = binding.startingAmount.editText!!.text.toString().toDouble()
            //val id = if (args.movementUI != null) args.movementUI!!.id else Constants.nan
            entryMovementViewModel.save(
                args.movementId,
                if (args.movementId == Constants.nan) startAmount else binding.leftAmount.editText?.text.toString().toDouble() ?: 0.0,
                startAmount,
                binding.currency.editText!!.text.toString(),
                binding.name.editText!!.text.toString(),
                binding.description.editText!!.text.toString()
            )
            findNavController().navigateUp()
        }
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
        binding.leftAmount.let {
            val leftText = it.editText?.text.toString()
            val startingText = binding.startingAmount.editText?.text.toString()
            // Checks left amount is
            if (args.movementId != Constants.nan && leftText.isEmpty()) {
                it.error = Strings.get(R.string.mandatory)
                return false
            }
            /*if (args.movementId != Constants.nan
                && (leftText.isNotEmpty() && startingText.isNotEmpty() && leftText.toDouble() <= startingText.toDouble())
            ) {
                it.error = Strings.get(R.string.mandatory)
                return false
            }*/
        }
        binding.currency.let {
            if (it.editText?.text.toString().isEmpty()) {
                it.error = Strings.get(R.string.mandatory)
                return false
            }
        }
        binding.category.let {
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
     * Triggers dialog warning left amount changing.
     */
    private fun warnLeftAmountChanging() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.action_cant_be_undone)
            .setMessage(R.string.warn_left_amount_changing_message)
            .setNegativeButton(Strings.get(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
            .setNeutralButton(Strings.get(R.string.reestablish)) { _, _ ->
                // Sets left amount to starting amount
                val originalLeftAmount = binding.leftAmount.editText?.text.toString()
                Snackbar.make(binding.entryMovementCardView, R.string.left_amount_was_set_back_to_starting_amount, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo) { binding.leftAmount.editText?.setText(originalLeftAmount) }
                    .show()
                binding.leftAmount.editText?.setText(binding.startingAmount.editText?.text.toString())
            }
            .setPositiveButton(Strings.get(R.string.edit)) { _, _ ->
                binding.leftAmount.editText?.apply {
                    if (!isEnabled) {
                        isEnabled = true
                        requestFocus()
                    }
                }
            }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        entryMovementViewModel.clearAllSelectedData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

