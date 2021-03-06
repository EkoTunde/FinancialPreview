package com.ekosoftware.financialpreview.ui.entry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.databinding.EntryFragmentAccountBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EntryAccountFragment : Fragment() {

    private enum class EditingState {
        NEW_ACCOUNT,
        EXISTING_ACCOUNT
    }

    private var _binding: EntryFragmentAccountBinding? = null
    private val binding get() = _binding!!

    private val args: EntryAccountFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EntryFragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val account = args.accountId
        /*TODO("Set account data in UI")
        binding.checkIsBankAccount.setOnCheckedChangeListener { _, isChecked ->
            extraOptionsVisible(isChecked)
        }*/
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

    private fun extraOptionsVisible(visible: Boolean) =
        listOf(binding.owner, binding.bankUniqueKey, binding.alias, binding.ownersId)
            .forEach { it.visibility = if (visible) View.VISIBLE else View.GONE }

}