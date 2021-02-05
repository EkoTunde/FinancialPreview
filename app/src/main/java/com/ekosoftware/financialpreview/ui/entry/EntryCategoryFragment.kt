package com.ekosoftware.financialpreview.ui.entry


import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Colors
import com.ekosoftware.financialpreview.core.ImageAdapter
import com.ekosoftware.financialpreview.data.local.CategoriesResourceInts
import com.ekosoftware.financialpreview.databinding.EntryFragmentCategoryBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

class EntryCategoryFragment : Fragment() {

    private enum class EditingState {
        NEW_CATEGORY,
        EXISTING_CATEGORY
    }

    private lateinit var dateResult: String

    private var _binding: EntryFragmentCategoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EntryFragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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
                        ResourcesCompat.getColor(
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
}