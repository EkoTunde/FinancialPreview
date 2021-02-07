package com.ekosoftware.financialpreview.core

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import com.ekosoftware.financialpreview.MainActivity
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Constants
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.presentation.CalculatorViewModel
import com.ekosoftware.financialpreview.presentation.FrequencyViewModel
import com.ekosoftware.financialpreview.presentation.SelectionViewModel
import com.ekosoftware.financialpreview.util.hasUselessDecimals
import com.ekosoftware.financialpreview.util.themeColor
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.transition.MaterialContainerTransform
import java.math.BigDecimal

abstract class BaseEntryFragment : Fragment() {

    val selectionViewModel: SelectionViewModel by activityViewModels()
    val frequencyViewModel: FrequencyViewModel by activityViewModels()
    val calculatorViewModel: CalculatorViewModel by activityViewModels()

    abstract val genericId: String

    abstract val toolbar: Toolbar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTransition()
        setUpToolbar()
        fetchSelectionData()
        setSelectionListeners()
        setCleanErrorOnChangedText()
    }

    abstract val addTitle: String
    abstract val editTitle: String

    open fun setUpToolbar() {
        toolbar.inflateMenu(if (genericId == Constants.nan) R.menu.save_menu else R.menu.save_delete_menu)
        (requireActivity() as MainActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        toolbar.title = if (genericId == Constants.nan) addTitle else editTitle
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(
            if (genericId == Constants.nan) {
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
            R.id.menu_item_save -> done()
            R.id.menu_item_delete -> delete()
            else -> super.onOptionsItemSelected(item)
        }
    }

    open fun done(): Boolean {
        return true
    }

    open fun delete(): Boolean = true

    abstract val startViewForTransition: View
    abstract val endViewForTransition: View
    abstract val targetId: Int

    /**
     * Set starting and ending transition for this fragment.
     */
    private fun setTransition() {
        enterTransition = MaterialContainerTransform().apply {
            startView = startViewForTransition
            endView = endViewForTransition
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            scrimColor = Color.TRANSPARENT
            containerColor = requireContext().themeColor(R.attr.colorSurface)
            startContainerColor = requireContext().themeColor(R.attr.colorSecondary)
            endContainerColor = requireContext().themeColor(R.attr.colorSurface)
        }
        returnTransition = Slide().apply {
            duration = resources.getInteger(R.integer.motion_duration_medium).toLong()
            addTarget(targetId)
        }
    }

    open fun fetchSelectionData() {
    }

    open fun setSelectionListeners() {
    }

    open fun setCleanErrorOnChangedText() {
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
    fun startAmountToString(amount: Double): String {
        return if (BigDecimal(amount).hasUselessDecimals()) {
            amount.toString().substring(0, amount.toString().indexOf("."))
        } else {
            amount.toString()
        }
    }

    /**
     * Sets navigation action behaviour for layouts user may use to edit data.
     */
    fun TextInputLayout.setNavigation(action: NavDirections) {
        (editText as? AutoCompleteTextView)?.let {
            isEndIconCheckable = false
        }
        setEndIconOnClickListener { findNavController().navigate(action) }
        arrayListOf(this, editText).forEach { it?.setOnClickListener { findNavController().navigate(action) } }
    }

}