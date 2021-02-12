package com.ekosoftware.financialpreview.ui.details

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ekosoftware.financialpreview.MainNavGraphDirections
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.app.Strings
import com.ekosoftware.financialpreview.core.Resource
import com.ekosoftware.financialpreview.data.model.settle.SettleGroup
import com.ekosoftware.financialpreview.databinding.DetailsFragmentSettleGroupBinding
import com.ekosoftware.financialpreview.presentation.DetailsViewModel
import com.ekosoftware.financialpreview.ui.details.adapter.MiniMovementsListAdapter
import com.ekosoftware.financialpreview.util.removeZeros
import com.ekosoftware.financialpreview.util.themeColor
import com.google.android.material.transition.MaterialContainerTransform
import com.leinardi.android.speeddial.SpeedDialActionItem

class DetailsSettleGroupFragment : Fragment() {
    private var _binding: DetailsFragmentSettleGroupBinding? = null
    private val binding get() = _binding!!

    private val args: DetailsSettleGroupFragmentArgs by navArgs()

    private val adapter: MiniMovementsListAdapter = MiniMovementsListAdapter { _, _ -> return@MiniMovementsListAdapter }

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
        _binding = DetailsFragmentSettleGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.navigationIcon.setOnClickListener { findNavController().navigateUp() }
        binding.movementsIncluded.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@DetailsSettleGroupFragment.adapter
        }
        fetchSettleGroupData()
        fetchMovementsData()
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
        binding.speedDial.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_add_to_group, R.drawable.ic_add)
                .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorWhite, requireContext().theme))
                .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.colorBlack, requireContext().theme))
                .setLabel(getString(R.string.add_to_group))
                .create()
        )
        binding.speedDial.setOnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.fab_settle -> {
                    binding.speedDial.close() // To close the Speed Dial with animation
                    return@setOnActionSelectedListener true // false will close it without animation
                }
                R.id.fab_edit -> {
                    val action = MainNavGraphDirections.actionGlobalEditSettleGroupFragment(args.settleGroupId)
                    findNavController().navigate(action)
                    binding.speedDial.close() // To close the Speed Dial with animation
                    return@setOnActionSelectedListener true // false will close it without animation
                }
                R.id.fab_add_to_group -> {
                    binding.speedDial.close() // To close the Speed Dial with animation
                    return@setOnActionSelectedListener true // false will close it without animation
                }
            }
            false
        }
    }

    private fun fetchSettleGroupData() = detailsViewModel.getSettleGroup(args.settleGroupId).observe(viewLifecycleOwner) { result ->
        when (result) {
            is Resource.Loading -> Unit
            is Resource.Success -> setSettleGroupData(result.data)
            is Resource.Failure -> Unit
        }
    }

    private fun fetchMovementsData() = detailsViewModel.getMovementsInSettleGroup(args.settleGroupId).observe(viewLifecycleOwner) { result ->
        when (result) {
            is Resource.Loading -> Unit
            is Resource.Success -> adapter.submitList(result.data)
            is Resource.Failure -> Unit
        }
    }

    private fun setSettleGroupData(settleGroup: SettleGroup) = with(binding) {
        name.text = settleGroup.name
        taxes.text = if (settleGroup.percentage > 0.0) Strings.get(
            R.string.settle_group_percent,
            settleGroup.percentage.toString().removeZeros(), "%"
        ) else Strings.get(R.string.settle_group_percent_0)
        description.text = settleGroup.description
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