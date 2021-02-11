package com.ekosoftware.financialpreview.ui.details

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ekosoftware.financialpreview.MainNavGraphDirections
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Colors
import com.ekosoftware.financialpreview.app.Constants.CATEGORY_ADJUSTMENT_ID
import com.ekosoftware.financialpreview.app.Constants.CATEGORY_DEBT_LOAN_ID
import com.ekosoftware.financialpreview.app.Constants.CATEGORY_TRANSFER_ID
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.data.model.record.RecordUI
import com.ekosoftware.financialpreview.databinding.DetailsFragmentRecordBinding
import com.ekosoftware.financialpreview.presentation.DetailsViewModel
import com.ekosoftware.financialpreview.presentation.SelectionViewModel
import com.ekosoftware.financialpreview.util.*
import com.google.android.material.transition.MaterialContainerTransform
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import java.text.SimpleDateFormat
import java.util.*

class DetailsRecordFragment : Fragment() {

    val args: DetailsRecordFragmentArgs by navArgs()

    private var _binding: DetailsFragmentRecordBinding? = null
    private val binding get() = _binding!!

    private val detailsViewModel: DetailsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DetailsFragmentRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.navigationIcon.setOnClickListener { findNavController().navigateUp() }
        setSpeedDial()
        detailsViewModel.getRecordUI(args.recordId).observe(viewLifecycleOwner) {
            setData(it)
        }
    }

    /**
     * Setups speed dial buttons and listeners when onClicked.
     */
    private fun setSpeedDial() {
        val ids = arrayOf(R.id.fab_edit, R.id.fab_restore)
        val drawables = arrayOf(R.drawable.ic_edit, R.drawable.ic_restore)
        val labels = arrayOf(R.string.edit, R.string.restore)
        listOf(0, 1).forEach { index -> addButton(ids[index], drawables[index], labels[index]) }
        binding.speedDial.setOnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.fab_edit -> {
                    val action = DetailsRecordFragmentDirections.actionDetailsRecordFragmentToEditRecordFragment(args.recordId)
                    findNavController().navigate(action)
                    binding.speedDial.close() // To close the Speed Dial with animation
                    return@setOnActionSelectedListener true // false will close it without animation
                }
                R.id.fab_restore -> {
                    try {
                        TODO("RESTORE")
                    } catch (e: Exception) {
                    }
                    /*val action =
                        MainNavGraphDirections.actionGlobalSelectFragment(SelectionViewModel.SETTLE_GROUPS_TO_ADD_TO_MOVEMENTS, args.movementId)
                    findNavController().navigate(action)*/
                    binding.speedDial.close() // To close the Speed Dial with animation
                    return@setOnActionSelectedListener true // false will close it without animation
                }
            }
            false
        }
    }

    /**
     * Adds a button with an id, drawable and string.
     */
    private fun addButton(id: Int, drawableResId: Int, stringResId: Int) {
        binding.speedDial.addActionItem(
            SpeedDialActionItem.Builder(id, drawableResId)
                .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorWhite, requireContext().theme))
                .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.colorBlack, requireContext().theme))
                .setLabel(Strings.get(stringResId))
                .create()
        )
    }

    private fun setData(recordUI: RecordUI) {
        binding.run {

            accountName.text = recordUI.accountName
            accountColor.setBackgroundColor(
                Colors.get(
                    recordUI.accountColorResId ?: R.color.colorPrimary
                )
            )
            amount.applyMoneyFormat(recordUI.currencyCode, recordUI.amount.forCommunicationAmount())
            recordUI.date?.let { date.text = SimpleDateFormat("yyyy-MM-dd ", Locale.getDefault()).format(it) }

            when (recordUI.categoryId) {
                CATEGORY_ADJUSTMENT_ID -> null
                CATEGORY_TRANSFER_ID -> recordUI.accountOutName?.let { Strings.get(R.string.transfer_from, it) }
                CATEGORY_DEBT_LOAN_ID -> when {
                    recordUI.debtorName != null -> {
                        Strings.get(R.string.you_ve_lend_to, recordUI.debtorName!!)
                    }
                    recordUI.lenderName != null -> {
                        Strings.get(R.string.debt_with, recordUI.lenderName!!)
                    }
                    else -> null
                }
                else -> recordUI.name
            }.let {
                if (it.isNullOrEmpty()) name.hide() else name.text = it
            }

            categoryName.text = recordUI.categoryName

            categoryColor.setCircleBackgroundColorResource(recordUI.categoryColorResId ?: R.color.colorPrimary)
            categoryIcon.setImageResource(recordUI.categoryIconResId ?: R.drawable.ic_movement)
            recordUI.description?.let {
                info.show()
                imageViewSubject.show()
                info.text = it
            }
            this
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        detailsViewModel.clearData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}