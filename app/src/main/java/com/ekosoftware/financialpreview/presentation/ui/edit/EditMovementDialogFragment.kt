package com.ekosoftware.financialpreview.presentation.ui.edit

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.core.ImageAdapter
import com.ekosoftware.financialpreview.data.local.CategoriesResourceInts
import com.ekosoftware.financialpreview.data.local.ColorsResourceInts
import com.ekosoftware.financialpreview.databinding.DialogFragmentEditMovementBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*


class EditMovementDialogFragment : DialogFragment() {

    private var _binding: DialogFragmentEditMovementBinding? = null
    private val binding get() = _binding!!

    private lateinit var dateResult: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentEditMovementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        return dialog
    }

    private var leftAmountEnable = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dateResult = ""

        binding.leftAmount.prefixText = "ARS "
        binding.startingAmount.prefixText = "ARS "

        binding.leftAmount.setEndIconOnClickListener {
            if (!binding.leftAmount.editText!!.isEnabled) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Esta acción no se puede deshacer")
                    .setMessage("Editar el saldo restante de este movimiento no crea ni quita un registro.")
                    .setNegativeButton(resources.getString(R.string.cancel)) { _, _ -> }
                    .setNeutralButton(resources.getString(R.string.reestablish)) { _, _ ->
                        Toast.makeText(
                            requireContext(),
                            "El saldo se equiparó al original.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .setPositiveButton(resources.getString(R.string.edit)) { _, _ ->
                        binding.leftAmount.editText?.apply {
                            if (!isEnabled) {
                                isEnabled = true
                                requestFocus()
                            }
                        }
                    }.show()
            }
            binding.leftAmount.editText!!.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    v.apply {
                        isEnabled = false
                        clearFocus()
                    }
                }
            }
        }
    }

    private fun showDialog() {
        val fragmentManager = requireActivity().supportFragmentManager
        //val newFragment = CalculatorDialogFragment()
        val newFragment = SelectionDialogFragment()

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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    var iconResId = R.drawable.ic_category

    private fun dialogChangeColor() =

        MaterialAlertDialogBuilder(requireContext())
            .setAdapter(object : ArrayAdapter<Int>(
                requireContext(),
                R.layout.item_dialog_color_selection,
                R.id.text_view,
                ColorsResourceInts.colorsIntIds
            ) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val v = super.getView(position, convertView, parent)
                    (v.findViewById<View>(R.id.text_view) as TextView).let {
                        it.setBackgroundColor(
                            getColor(
                                resources,
                                ColorsResourceInts.colorsIntIds[position],
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
                    ContextCompat.getColor(requireContext(), ColorsResourceInts.colorsIntIds[item])
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
}

