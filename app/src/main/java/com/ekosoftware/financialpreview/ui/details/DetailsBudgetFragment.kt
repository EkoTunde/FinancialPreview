package com.ekosoftware.financialpreview.ui.details

import android.content.res.ColorStateList
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
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.core.Resource
import com.ekosoftware.financialpreview.data.model.budget.Budget
import com.ekosoftware.financialpreview.databinding.DetailsFragmentBudgetBinding
import com.ekosoftware.financialpreview.presentation.DetailsViewModel
import com.ekosoftware.financialpreview.util.*
import com.google.android.material.transition.MaterialContainerTransform
import com.leinardi.android.speeddial.SpeedDialActionItem
import java.math.BigDecimal
import java.math.RoundingMode

class DetailsBudgetFragment : Fragment() {
    private var _binding: DetailsFragmentBudgetBinding? = null
    private val binding get() = _binding!!

    private val args: DetailsBudgetFragmentArgs by navArgs()

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DetailsFragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchData()
        binding.navigationIcon.setOnClickListener { findNavController().navigateUp() }
        binding.speedDial.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_settle, R.drawable.ic_check)
                .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorWhite, requireContext().theme))
                .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.colorBlack, requireContext().theme))
                .setLabel(getString(R.string.settle))
                .create()
        )
        binding.speedDial.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_edit, R.drawable.ic_edit)
                .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorWhite, requireContext().theme))
                .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.colorBlack, requireContext().theme))
                .setLabel(getString(R.string.edit))
                .create()
        )

        binding.speedDial.setOnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.fab_create_record_from_budget -> {
                    binding.speedDial.close() // To close the Speed Dial with animation
                    return@setOnActionSelectedListener true // false will close it without animation
                }
                R.id.fab_edit -> {
                    val action = MainNavGraphDirections.actionGlobalEditBudgetFragment(args.budgetId)
                    findNavController().navigate(action)
                    binding.speedDial.close() // To close the Speed Dial with animation
                    return@setOnActionSelectedListener true // false will close it without animation
                }
            }
            false
        }
    }

    private fun fetchData() = detailsViewModel.getBudget(args.budgetId).observe(viewLifecycleOwner) { result ->
        when (result) {
            is Resource.Loading -> {
            }
            is Resource.Success -> {
                setData(result.data)
            }
            is Resource.Failure -> {
            }
        }
    }

    private fun setData(budget: Budget) = with(binding) {

        leftAmount.applyMoneyFormat(budget.currencyCode, budget.leftAmount.forCommunicationAmount())
        leftAmount.setTextColor(Colors.get(if (budget.leftAmount < 0) R.color.colorAmountNegative else R.color.colorAmountPositive))
        startingAmount.applyMoneyFormatAndSpecifiedStart(
            Strings.get(R.string.of_),
            budget.currencyCode,
            budget.startingAmount.forCommunicationAmount()
        )
        //startingAmount.setTextColor(Colors.get(if (budget.startingAmount < 0) R.color.colorAmountNegative else R.color.colorAmountPositive))

        name.text = budget.name

        budget.description?.takeIf { it.isNotEmpty() }?.let {
            description.text = it
            description.show()
            imageViewQuote.show()
        }

        val percentLeft = BigDecimal(budget.leftAmount.toString()).times(BigDecimal("100.00")).div(BigDecimal(budget.startingAmount.toString()))
            .setScale(2, RoundingMode.DOWN)

        val progressColor = Colors.progressColor(percentLeft.toInt())
        leftProgress.progress = percentLeft.toInt()
        leftProgress.progressTintList = ColorStateList.valueOf(progressColor)

        txtProgress.text = Strings.get(R.string.budget_percent, percentLeft, "%")

        monthsIncluded.text = budget.frequency.forDisplay()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clears live data objects
        detailsViewModel.clearData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}