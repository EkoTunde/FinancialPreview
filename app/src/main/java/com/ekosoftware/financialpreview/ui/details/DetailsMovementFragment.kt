package com.ekosoftware.financialpreview.ui.details

import android.graphics.Color
import android.os.Bundle
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
import com.ekosoftware.financialpreview.app.Constants
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.core.Resource
import com.ekosoftware.financialpreview.data.model.movement.Frequency
import com.ekosoftware.financialpreview.data.model.movement.MovementUI
import com.ekosoftware.financialpreview.databinding.DetailsFragmentMovementBinding
import com.ekosoftware.financialpreview.presentation.DetailsViewModel
import com.ekosoftware.financialpreview.presentation.SelectionViewModel
import com.ekosoftware.financialpreview.util.*
import com.google.android.material.transition.MaterialContainerTransform
import com.leinardi.android.speeddial.SpeedDialActionItem

class DetailsMovementFragment : Fragment() {
    private var _binding: DetailsFragmentMovementBinding? = null
    private val binding get() = _binding!!

    private val args: DetailsMovementFragmentArgs by navArgs()

    private val detailsViewModel: DetailsViewModel by activityViewModels()
    private val selectionViewModel: SelectionViewModel by activityViewModels()

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
        _binding = DetailsFragmentMovementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        detailsViewModel.setMovementId(args.movementId)
        setSpeedDial()
        fetchData()
        binding.navigationIcon.setOnClickListener { findNavController().navigateUp() }
        observeAddToSettleGroup()
    }

    /**
     * Setups speed dial buttons and listeners when onClicked.
     */
    private fun setSpeedDial() {
        val ids = arrayOf(R.id.fab_settle, R.id.fab_edit, R.id.fab_add_to_group)
        val drawables = arrayOf(R.drawable.ic_check, R.drawable.ic_edit, R.drawable.ic_add)
        val labels = arrayOf(R.string.settle, R.string.edit, R.string.add_to_group)
        listOf(0, 1, 2).forEach { index ->
            addButton(ids[index], drawables[index], labels[index])
        }
        binding.speedDial.setOnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.fab_settle -> {
                    val action = MainNavGraphDirections.actionGlobalSettleFragment(Constants.SETTLE_TYPE_MOVEMENT, args.movementId,0)
                    findNavController().navigate(action)
                    binding.speedDial.close() // To close the Speed Dial with animation
                    return@setOnActionSelectedListener true // false will close it without animation
                }
                R.id.fab_edit -> {
                    val action = MainNavGraphDirections.actionGlobalEditMovementFragment(args.movementId)
                    findNavController().navigate(action)
                    binding.speedDial.close() // To close the Speed Dial with animation
                    return@setOnActionSelectedListener true // false will close it without animation
                }
                R.id.fab_add_to_group -> {
                    val action =
                        MainNavGraphDirections.actionGlobalSelectFragment(SelectionViewModel.SETTLE_GROUPS_TO_ADD_TO_MOVEMENTS, args.movementId)
                    findNavController().navigate(action)
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

    private fun fetchData() = detailsViewModel.movementUI.observe(viewLifecycleOwner) { result ->
        when (result) {
            is Resource.Loading -> {
            }
            is Resource.Success -> setData(result.data)
            is Resource.Failure -> {
            }
        }
    }

    private fun setData(movementUI: MovementUI) = binding.run {
        movementUI.accountColorResId?.let {
            accountColor.setBackgroundColor(Colors.get(it))
            accountColor.show()
        }
        movementUI.accountName?.let {
            accountName.text = it
            accountName.show()
        }

        leftAmount.applyMoneyFormat(movementUI.currencyCode, movementUI.leftAmount.forCommunicationAmount())
        leftAmount.setTextColor(Colors.get(if (movementUI.leftAmount < 0) R.color.colorAmountNegative else R.color.colorAmountPositive))
        startingAmount.applyMoneyFormatAndSpecifiedStart(
            Strings.get(R.string.of_),
            movementUI.currencyCode,
            movementUI.startingAmount.forCommunicationAmount()
        )
        //startingAmount.setTextColor(Colors.get(if (movementUI.startingAmount < 0) R.color.colorAmountNegative else R.color.colorAmountPositive))

        val fullName = movementUI.name + movementUI.installmentsCalc()
        name.text = fullName

        movementUI.categoryName?.takeUnless { it.isEmpty() }?.let {
            categoryName.text = it
            categoryColor.setCircleBackgroundColorResource(movementUI.categoryColorResId!!)
            categoryIcon.setImageResource(movementUI.categoryIconResId!!)
            arrayOf(categoryName, categoryColor, categoryIcon).show()
        }

        movementUI.budgetName?.let {
            budgetName.text = it
            arrayOf(budgetName, imageViewSub).show()
        }

        monthsIncluded.text = Frequency(
            movementUI.fromYearMonth,
            movementUI.toYearMonth,
            movementUI.totalInstallments,
            movementUI.monthsChecked
        ).forDisplay()
    }

    private fun observeAddToSettleGroup() {
        selectionViewModel.getSettleGroupId().observe(viewLifecycleOwner) {
            it?.let { settleGroupId ->
                detailsViewModel.addSettleGroupAndMovementRef(settleGroupId, args.movementId)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        selectionViewModel.clearData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}