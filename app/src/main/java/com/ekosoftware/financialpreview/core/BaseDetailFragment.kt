package com.ekosoftware.financialpreview.core

import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ekosoftware.financialpreview.MainNavGraphDirections
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.presentation.SelectionViewModel
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView

abstract class BaseDetailFragment : Fragment() {

    open fun speedDialIds() : Array<Int>? = null

    open fun speedDialDrawables() : Array<Int>? = null

    open fun speedDialLabels() : Array<Int>? = null

    open fun speedDialButtonsData() : List<Array<Int>>? = null

    /**
     * Setups speed dial buttons and listeners when onClicked.
     */
    private fun SpeedDialView.setSpeedDial(genericId: String) {
        val ids = arrayOf(R.id.fab_settle, R.id.fab_edit, R.id.fab_add_to_group)
        val drawables = arrayOf(R.drawable.ic_check, R.drawable.ic_edit, R.drawable.ic_add)
        val labels = arrayOf(R.string.settle, R.string.edit, R.string.add_to_group)
        listOf(0, 1, 2).forEach { index ->
            addButton(ids[index], drawables[index], labels[index])
        }
        this.setOnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.fab_settle -> {
                    Toast.makeText(requireContext(), "Liquidarrr", Toast.LENGTH_SHORT).show()
                    this.close() // To close the Speed Dial with animation
                    return@setOnActionSelectedListener true // false will close it without animation
                }
                R.id.fab_edit -> {
                    val action = MainNavGraphDirections.actionGlobalEditMovementFragment(genericId)
                    findNavController().navigate(action)
                    this.close() // To close the Speed Dial with animation
                    return@setOnActionSelectedListener true // false will close it without animation
                }
                R.id.fab_add_to_group -> {
                    val action =
                        MainNavGraphDirections.actionGlobalSelectFragment(SelectionViewModel.SETTLE_GROUPS_TO_ADD_TO_MOVEMENTS, genericId)
                    findNavController().navigate(action)
                    this.close() // To close the Speed Dial with animation
                    return@setOnActionSelectedListener true // false will close it without animation
                }
            }
            false
        }
    }

    /**
     * Adds a button with an id, drawable and string.
     */
    private fun SpeedDialView.addButton(id: Int, drawableResId: Int, stringResId: Int) {
        this.addActionItem(
            SpeedDialActionItem.Builder(id, drawableResId)
                .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorWhite, requireContext().theme))
                .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.colorBlack, requireContext().theme))
                .setLabel(Strings.get(stringResId))
                .create()
        )
    }
}