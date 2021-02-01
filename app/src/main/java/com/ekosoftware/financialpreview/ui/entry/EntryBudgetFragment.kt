package com.ekosoftware.financialpreview.ui.entry

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.transition.Slide
import com.ekosoftware.financialpreview.R
import com.ekosoftware.financialpreview.databinding.EntryFragmentBudgetBinding
import com.ekosoftware.financialpreview.util.themeColor
import com.google.android.material.transition.MaterialContainerTransform

class EntryBudgetFragment : Fragment() {

    private enum class EditingState {
        NEW_BUDGET,
        EXISTING_BUDGET
    }

    private var _binding: EntryFragmentBudgetBinding? = null
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
        _binding = EntryFragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*enterTransition = MaterialContainerTransform().apply {
            startView = requireActivity().findViewById(R.id.main_fab)*//*startingView*//*
            endView = binding.entryMovementCardView
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            scrimColor = Color.TRANSPARENT
            containerColor = requireContext().themeColor(R.attr.colorSurface)
            startContainerColor = requireContext().themeColor(R.attr.colorSecondary)
            endContainerColor = requireContext().themeColor(R.attr.colorSurface)
        }
        returnTransition = Slide().apply {
            duration = resources.getInteger(R.integer.motion_duration_medium).toLong()
            addTarget(R.id.entryMovementCardView)
        }*/
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}