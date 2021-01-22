package com.ekosoftware.financialpreviewdesign

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.databinding.FragmentEditAccountBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EditAccountDialogFragment : DialogFragment() {

    private var _binding: FragmentEditAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        return dialog
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.checkIsBankAccount.setOnCheckedChangeListener { _, isChecked ->
            extraOptionsVisible(isChecked)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // When menu's delete option is pressed
    private fun showDeleteConfirmationDialog() = MaterialAlertDialogBuilder(requireContext())
        .setTitle("Esta acción no se puede deshacer")
        .setMessage("Si eliminas esta cuenta perderas todos los registros asociados a ella")
        .setNegativeButton(resources.getString(R.string.cancel), null)
        .setPositiveButton(resources.getString(R.string.edit)) { _, _ ->
            // Delete
        }.show()

    private fun extraOptionsVisible(visible: Boolean) = listOf(binding.owner, binding.bankUniqueKey, binding.alias, binding.ownersId)
        .forEach { it.visibility = if (visible) View.VISIBLE else View.GONE }

}