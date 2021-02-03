package com.ekosoftware.financialpreview.ui.entry

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Slide
import com.ekosoftware.financialpreview.MainActivity
import com.ekosoftware.financialpreview.MainNavGraphDirections
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Colors
import com.ekosoftware.financialpreview.app.Constants
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.core.ImageAdapter
import com.ekosoftware.financialpreview.data.local.CategoriesResourceInts
import com.ekosoftware.financialpreview.data.model.movement.Movement
import com.ekosoftware.financialpreview.databinding.EntryFragmentMovementBinding
import com.ekosoftware.financialpreview.presentation.EntryMovementViewModel
import com.ekosoftware.financialpreview.presentation.SelectionViewModel
import com.ekosoftware.financialpreview.util.forDisplay
import com.ekosoftware.financialpreview.util.forDisplayAmount
import com.ekosoftware.financialpreview.util.show
import com.ekosoftware.financialpreview.util.themeColor
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class EntryMovementFragment : Fragment() {

    private enum class EditingState {
        NEW_MOVEMENT,
        EXISTING_MOVEMENT
    }

    companion object {
        private const val TAG = "EntryMovementFragment"
    }

    private var _binding: EntryFragmentMovementBinding? = null
    private val binding get() = _binding!!

    private val entryMovementViewModel: EntryMovementViewModel by activityViewModels()

    private val selectionViewModel: SelectionViewModel by activityViewModels()

    private lateinit var dateResult: String

    private val args: EntryMovementFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = EntryFragmentMovementBinding.inflate(inflater, container, false)
        binding.toolbar.inflateMenu(R.menu.save_menu)
        (requireActivity() as MainActivity).setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.toolbar.title =
            if (args.movementId == Constants.nan) Strings.get(R.string.title_add_movement) else Strings.get(R.string.title_edit_movement)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setTransition() // Run Animation

        if (args.movementId != Constants.nan) {
            Log.d(TAG, "onViewCreated: ID es ${args.movementId}")
            binding.leftAmount.show()
            entryMovementViewModel.get(args.movementId).observe(viewLifecycleOwner) { movement ->
                movement?.let {
                    populateUI(it)
                    entryMovementViewModel.movementRetrievedOk()
                    entryMovementViewModel.get(args.movementId).removeObservers(viewLifecycleOwner)
                }
            }
        }
        fetchSelectionData()
        setCurrencyListener()
        setAccountListener()
        setFrequencyListener()
        setCategoryListener()
        setBudgetListener()

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

    private fun fetchSelectionData() {
        selectionViewModel.accountId.observe(viewLifecycleOwner) { accountId ->
            binding.currency.isEnabled = accountId == null
            entryMovementViewModel.setAccountId(accountId)
        }
        selectionViewModel.budgetId.observe(viewLifecycleOwner) { id -> entryMovementViewModel.setBudgetId(id) }
        selectionViewModel.categoryId.observe(viewLifecycleOwner) { id -> entryMovementViewModel.setCategoryId(id) }
        selectionViewModel.currencyId.observe(viewLifecycleOwner) { id -> entryMovementViewModel.setCurrencyId(id) }
        entryMovementViewModel.frequency.observe(viewLifecycleOwner) { frequency -> binding.frequency.editText?.setText(frequency.forDisplay()) }
        entryMovementViewModel.accountName().observe(viewLifecycleOwner) { name -> binding.account.editText?.setText(name) }
        entryMovementViewModel.budgetName().observe(viewLifecycleOwner) { name -> binding.budget.editText?.setText(name) }
        entryMovementViewModel.categoryName().observe(viewLifecycleOwner) { name -> binding.category.editText?.setText(name) }
        entryMovementViewModel.currencyCode().observe(viewLifecycleOwner) { code -> binding.currency.editText?.setText(code) }
    }

    private fun populateUI(movement: Movement) = with(binding) {
        leftAmount.editText?.setText(movement.leftAmount.forDisplayAmount(movement.currencyCode))
        currency.editText?.setText(movement.currencyCode)
        startingAmount.editText?.setText(movement.startingAmount.forDisplayAmount(movement.currencyCode))
        frequency.editText?.setText(movement.frequency?.forDisplay())
        description.editText?.setText(movement.description)
        movement.accountId?.let {
            entryMovementViewModel.setAccountId(it)
            currency.isEnabled = false
        }
        movement.categoryId?.let { entryMovementViewModel.setCategoryId(it) }
        movement.budgetId?.let { entryMovementViewModel.setBudgetId(it) }
    }

    private fun setCurrencyListener() = with(binding.currency) {
        isEndIconCheckable = false
        val action = MainNavGraphDirections.actionGlobalSelectFragment(SelectionViewModel.CURRENCIES)
        setEndIconOnClickListener { findNavController().navigate(action) }
        arrayListOf(this, editText).forEach { it?.setOnClickListener { findNavController().navigate(action) } }
    }

    private fun setAccountListener() = with(binding.account) {
        isEndIconCheckable = false
        val action = MainNavGraphDirections.actionGlobalSelectFragment(SelectionViewModel.ACCOUNTS)
        setEndIconOnClickListener { findNavController().navigate(action) }
        arrayListOf(this, editText).forEach { it?.setOnClickListener { findNavController().navigate(action) } }
    }

    private fun setFrequencyListener() = with(binding.frequency) {
        val action = EntryMovementFragmentDirections.actionEditMovementToEditFrequency(null)
        setEndIconOnClickListener { findNavController().navigate(action) }
        arrayListOf(this, editText).forEach { it?.setOnClickListener { findNavController().navigate(action) } }
    }

    private fun setCategoryListener() = with(binding.category) {
        isEndIconCheckable = false
        val action = MainNavGraphDirections.actionGlobalSelectFragment(SelectionViewModel.CATEGORIES)
        setEndIconOnClickListener { findNavController().navigate(action) }
        arrayListOf(this, editText).forEach { it?.setOnClickListener { findNavController().navigate(action) } }
    }

    private fun setBudgetListener() = with(binding.budget) {
        isEndIconCheckable = false
        val action = MainNavGraphDirections.actionGlobalSelectFragment(SelectionViewModel.BUDGETS)
        setEndIconOnClickListener { findNavController().navigate(action) }
        arrayListOf(this, editText).forEach { it?.setOnClickListener { findNavController().navigate(action) } }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_save -> {
                done()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun done() {
        if (isRequiredInfoCompleted()) {
            val prefix = if (binding.chipExpense.isChecked) "-" else ""
            val startAmount = (prefix + binding.startingAmount.editText!!.text.toString()).toDouble()
            val id = if (args.movementUI != null) args.movementUI!!.id else Constants.nan
            entryMovementViewModel.save(
                id,
                if (args.movementId == Constants.nan) startAmount else binding.leftAmount.editText!!.text.toString().toDouble(),
                startAmount,
                binding.currency.editText!!.text.toString(),
                binding.name.editText!!.text.toString(),
                binding.description.editText!!.text.toString()
            )
            //findNavController().navigateUp()
        }
    }

    private fun isRequiredInfoCompleted(): Boolean {
        /*with(binding) {
            arrayOf(binding.startingAmount, binding.leftAmount, binding.currency, binding.category, binding.name, binding.frequency).forEach {
                if (it.editText?.text.toString().isNotEmpty()) {
                    it.error = Strings.get(R.string.mandatory)
                    return false
                }
            }
        }*/
        binding.startingAmount.let {
            if (it.editText?.text.toString().isEmpty()) {
                it.error = Strings.get(R.string.mandatory)
                return false
            }
        }
        binding.leftAmount.let {
            if (args.movementId != Constants.nan && it.editText?.text.toString().isEmpty()) {
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

    /*private fun showDialog() {
        val fragmentManager = requireActivity().supportFragmentManager
        //val newFragment = CalculatorDialogFragment()
        val newFragment = SelectionFragment()

        // The device is smaller, so show the fragment fullscreen
        val transaction = fragmentManager.beginTransaction()
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction
            .add(android.R.id.content, newFragment)
            .addToBackStack(null)
            .commit()
    }*/

    private fun yearMonthChooser() = AlertDialog.Builder(requireContext()).apply {
        setTitle("Prueba")
        val inflater = requireActivity().layoutInflater
        val dialogView: View =
            inflater.inflate(R.layout.alert_dialog_date_selector, binding.root, false)
        setView(dialogView)

        val c = Calendar.getInstance()
        val currentYear = c.get(Calendar.YEAR)
        val currentMonth = c.get(Calendar.MONTH) + 1

        val monthPicker = dialogView.findViewById<View>(R.id.month_picker) as NumberPicker
        monthPicker.maxValue = 12
        monthPicker.minValue = 1
        monthPicker.value = currentMonth
        val yearPicker = dialogView.findViewById<View>(R.id.year_picker) as NumberPicker

        yearPicker.maxValue = 2500
        yearPicker.minValue = 1900
        yearPicker.value = currentYear

        setPositiveButton("Ok") { dialog, _ ->
            dialog.dismiss()
            dateResult = "${monthPicker.value}/${yearPicker.value}"
            Toast.makeText(requireContext(), dateResult, Toast.LENGTH_SHORT).show()
            //binding.account2.editText?.setText(dateResult)
        }
        setOnDismissListener {
            //(binding.account2.editText as? AutoCompleteTextView)?.clearFocus()
        }

    }

    var iconResId = R.drawable.ic_category

    private fun dialogChangeColor() = MaterialAlertDialogBuilder(requireContext())
        .setAdapter(object : ArrayAdapter<Int>(
            requireContext(),
            R.layout.item_dialog_color_selection,
            R.id.text_view,
            Colors.colorsResIds
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                (v.findViewById<View>(R.id.text_view) as TextView).let {
                    it.setBackgroundColor(
                        getColor(
                            resources,
                            Colors.colorsResIds[position],
                            null
                        )
                    )
                    it.text = ""
                }
                return v
            }
        }) { dialog, item ->
            // Save the color and dismiss dialog
            val color =
                Colors.get(Colors.colorsResIds[item])
            // Do something
            Toast.makeText(requireContext(), "$item", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        .setNegativeButton(resources.getString(R.string.cancel), null).create().show()

    private fun dialogChangeIcon() {

        var iconDialog: AlertDialog? = null
        val builder = AlertDialog.Builder(requireContext())

        val gridView = GridView(requireContext())
        val iconItems =
            ImageAdapter(requireContext(), CategoriesResourceInts.categoriesIntIds.toMutableList())
        gridView.apply {
            adapter = iconItems
            numColumns = 4
            choiceMode = GridView.CHOICE_MODE_SINGLE
            onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                // Do something
                iconDialog!!.dismiss()
            }
        }

        builder.setView(gridView).setTitle("Select an icon")
        iconDialog = builder.create()
        iconDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        selectionViewModel.clearSelectedData()
        entryMovementViewModel.clearAllSelectedData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

