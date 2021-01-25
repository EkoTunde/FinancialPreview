package com.ekosoftware.financialpreview.ui.edit

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Colors
import com.ekosoftware.financialpreview.core.ImageAdapter
import com.ekosoftware.financialpreview.data.local.CategoriesResourceInts
import com.ekosoftware.financialpreview.databinding.EditFragmentMovementBinding
import com.ekosoftware.financialpreview.presentation.ShareViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*


class EditMovementFragment : Fragment() {

    private var _binding: EditFragmentMovementBinding? = null
    private val binding get() = _binding!!

    private val shareViewModel by activityViewModels<ShareViewModel>()

    private lateinit var dateResult: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EditFragmentMovementBinding.inflate(inflater, container, false)
        binding.toolbar.inflateMenu(R.menu.save_menu)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding.account.apply {
            setEndIconOnClickListener {
                findNavController().navigate(R.id.editMovement_to_selection)
            }
            arrayListOf(this, editText).forEach {
                it?.setOnClickListener {
                    findNavController().navigate(R.id.editMovement_to_selection)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_save -> {
                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private var leftAmountEnable = false

    private fun showDialog() {
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
    }

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

    private fun dialogChangeColor() =

        MaterialAlertDialogBuilder(requireContext())
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

