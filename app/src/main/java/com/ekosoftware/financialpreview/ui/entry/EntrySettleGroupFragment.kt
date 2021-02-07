package com.ekosoftware.financialpreview.ui.entry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.ekosoftware.financialpreview.MainNavGraphDirections
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Colors
import com.ekosoftware.financialpreview.app.Constants
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.core.BaseEntryFragment
import com.ekosoftware.financialpreview.data.model.settle.SettleGroup
import com.ekosoftware.financialpreview.data.model.settle.SettleGroupWithMovements
import com.ekosoftware.financialpreview.databinding.EntryFragmentSettleGroupBinding
import com.ekosoftware.financialpreview.presentation.EntrySettleGroupViewModel
import com.ekosoftware.financialpreview.ui.entry.adapter.DeletableMovementListAdapter
import com.ekosoftware.financialpreview.ui.entry.adapter.SettleGroupsAttributesAdapter
import com.ekosoftware.financialpreview.util.hide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class EntrySettleGroupFragment : BaseEntryFragment() {

    private var _binding: EntryFragmentSettleGroupBinding? = null
    private val binding get() = _binding!!

    private val entrySettleGroupViewModel: EntrySettleGroupViewModel by activityViewModels()

    private val args: EntrySettleGroupFragmentArgs by navArgs()

    override val genericId: String get() = args.settleGroupId
    override val toolbar: Toolbar get() = binding.toolbar
    override val startViewForTransition: View get() = requireActivity().findViewById(R.id.main_fab)
    override val endViewForTransition: View get() = binding.entrySettleGroupCardView
    override val targetId: Int get() = R.id.entrySettleGroupCardView
    override val addTitle: String get() = Strings.get(R.string.add_settle_group)
    override val editTitle: String get() = Strings.get(R.string.title_edit_settle_group)

    private val attributesAdapter: SettleGroupsAttributesAdapter by lazy { SettleGroupsAttributesAdapter(attributesAdapterListener) }

    private val attributesAdapterListener: SettleGroupsAttributesAdapter.OnAttributesListener =
        object : SettleGroupsAttributesAdapter.OnAttributesListener {
            override fun onNameChanged(name: String) = entrySettleGroupViewModel.setName(name)
            override fun onDescriptionChange(description: String) = entrySettleGroupViewModel.setDescription(description)
            override fun onPercentageChanged(percentage: Double) = entrySettleGroupViewModel.setPercentage(percentage)
            override fun onChangeColor() = dialogChangeColor()
        }

    private val deletableMovementListAdapter: DeletableMovementListAdapter = DeletableMovementListAdapter { movementId ->
        args.settleGroupId.takeIf { it != Constants.nan }?.let { settleGroupId -> entrySettleGroupViewModel.deleteRef(settleGroupId, movementId) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = EntryFragmentSettleGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (args.settleGroupId != Constants.nan) {
            entrySettleGroupViewModel.get(args.settleGroupId).observe(viewLifecycleOwner) { settleGroupWithMovements ->
                settleGroupWithMovements?.let {
                    populateUI(it)
                    entrySettleGroupViewModel.setSettleGroupAttrs(it.settleGroup)
                }
            }
        }
        binding.rvSelectedMovements.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ConcatAdapter(attributesAdapter, deletableMovementListAdapter)
        }
    }

    private fun populateUI(settleGroupWithMovements: SettleGroupWithMovements) {
        attributesAdapter.updateAttrs(settleGroupWithMovements.settleGroup)
        deletableMovementListAdapter.submitList(settleGroupWithMovements.movements)
    }

    override fun fetchSelectionData() {
        entrySettleGroupViewModel.getColorResId().observe(viewLifecycleOwner) {
            it?.let { colorResId -> attributesAdapter.updateColor(colorResId) }
        }
    }

    private fun dialogChangeColor() = MaterialAlertDialogBuilder(requireContext())
        .setAdapter(object :
            ArrayAdapter<Int>(requireContext(), R.layout.item_color_selection, R.id.text_view, Colors.colorsResIds) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                (v.findViewById<View>(R.id.text_view) as TextView).let {
                    it.setBackgroundColor(Colors.get(Colors.colorsResIds[position])) /*ResourcesCompat.getColor(resources, Colors.colorsResIds[position], null)*/
                    it.text = ""
                }
                return v
            }
        }) { dialog, item ->
            entrySettleGroupViewModel.setColorResId(Colors.colorsResIds[item])
            dialog.dismiss()
        }
        .setNegativeButton(Strings.get(R.string.cancel), null)
        .create()
        .show()

    override fun done(): Boolean {
        if (entrySettleGroupViewModel.isRequiredInfoFilled()) {
            entrySettleGroupViewModel.save(args.settleGroupId)
            findNavController().navigateUp()
        } else {
            Snackbar.make(binding.root, R.string.every_field_is_required, Snackbar.LENGTH_LONG).show()
        }
        return super.done()
    }

    override fun delete(): Boolean {
        entrySettleGroupViewModel.delete(args.settleGroupId)
        findNavController().navigate(MainNavGraphDirections.actionGlobalPendingPageFragment())
        return super.delete()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        entrySettleGroupViewModel.clearAllSelectedData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}