package com.ekosoftware.financialpreview.ui.entry

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Slide
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

    private var _binding: EntryFragmentMovementBinding? = null
    private val binding get() = _binding!!

    private val entryMovementViewModel: EntryMovementViewModel by activityViewModels()

    private val selectionViewModel: SelectionViewModel by activityViewModels()

    private lateinit var dateResult: String

    private val args: EntryMovementFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = EntryFragmentMovementBinding.inflate(inflater, container, false)
        binding.toolbar.inflateMenu(R.menu.save_menu)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.toolbar.title = if (args.movementId == "NaN") Strings.get(R.string.title_add_movement) else Strings.get(R.string.title_edit_movement)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setTransition() // Run Animation

        fetchSelectionData()
        if (args.movementId != "NaN") {
            binding.leftAmount.show()
            entryMovementViewModel.setMovementId(args.movementId)
            entryMovementViewModel.movement.observe(viewLifecycleOwner) { movement -> movement?.let { populateUI(it) } }
        }

        setCurrencyAdapter()
        setAccountListener()    // Sets behavior
        setFrequencyListener()  // Sets behavior
        setCategoryListener()   // Sets behavior
        setBudgetListener()     // Sets behavior
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

    private fun setCurrencyAdapter() {
        val items = Currency.getAvailableCurrencies().map { it.currencyCode }
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_list_item, items)
        (binding.currency.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    private fun fetchSelectionData() {
        selectionViewModel.accountId.observe(viewLifecycleOwner) { entryMovementViewModel.setAccountId(it) }
        selectionViewModel.categoryId.observe(viewLifecycleOwner) { entryMovementViewModel.setCategoryId(it) }
        selectionViewModel.budgetId.observe(viewLifecycleOwner) { entryMovementViewModel.setBudgetId(it) }
        entryMovementViewModel.frequency.observe(viewLifecycleOwner) { binding.repetition.editText?.setText(it.forDisplay()) }
        entryMovementViewModel.accountName.observe(viewLifecycleOwner) { binding.account.editText?.setText(it) }
        entryMovementViewModel.categoryName.observe(viewLifecycleOwner) { binding.category.editText?.setText(it) }
        entryMovementViewModel.budgetName.observe(viewLifecycleOwner) { binding.budget.editText?.setText(it) }
    }

    private fun populateUI(movement: Movement) = binding.run {
        leftAmount.editText?.setText(movement.leftAmount.forDisplayAmount(movement.currencyCode))
        currency.editText?.setText(movement.currencyCode)
        startingAmount.editText?.setText(movement.startingAmount.forDisplayAmount(movement.currencyCode))
        repetition.editText?.setText(movement.frequency?.forDisplay())
        description.editText?.setText(movement.description)
        movement.accountId?.let { entryMovementViewModel.setAccountId(it) }
        movement.categoryId?.let { entryMovementViewModel.setCategoryId(it) }
        movement.budgetId?.let { entryMovementViewModel.setBudgetId(it) }
    }

    private fun setAccountListener() = binding.account.apply {
        val action = MainNavGraphDirections.actionGlobalSelectFragment(SelectionViewModel.ACCOUNTS)
        setEndIconOnClickListener { findNavController().navigate(action) }
        arrayListOf(this, editText).forEach { it?.setOnClickListener { findNavController().navigate(action) } }
    }

    private fun setFrequencyListener() = binding.repetition.apply {
        val action = EntryMovementFragmentDirections.actionEditMovementFragmentToEditFrequencyFragment4()
        setEndIconOnClickListener { findNavController().navigate(action) }
        arrayListOf(this, editText).forEach { it?.setOnClickListener { findNavController().navigate(action) } }
    }

    private fun setCategoryListener() = binding.category.apply {
        val action = MainNavGraphDirections.actionGlobalSelectFragment(SelectionViewModel.CATEGORIES)
        setEndIconOnClickListener { findNavController().navigate(action) }
        arrayListOf(this, editText).forEach { it?.setOnClickListener { findNavController().navigate(action) } }
    }

    private fun setBudgetListener() = binding.budget.apply {
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

    private fun done() = binding.toolbar.setOnClickListener {
        val startAmount = binding.startingAmount.editText!!.text.toString().toDouble()
        val id = if (args.movementUI != null) args.movementUI!!.id else Constants.nan
        entryMovementViewModel.save(
            id,
            if (args.movementId == "NaN") startAmount else binding.leftAmount.editText!!.text.toString().toDouble(),
            startAmount,
            binding.currency.editText!!.text.toString(),
            binding.name.editText!!.text.toString(),
            binding.description.editText!!.text.toString()
        )
        findNavController().navigateUp()
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

