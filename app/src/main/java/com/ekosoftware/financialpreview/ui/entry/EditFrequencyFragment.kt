package com.ekosoftware.financialpreview.ui.entry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.ekosoftware.financialpreview.databinding.EditFragmentFrequencyBinding
import com.ekosoftware.financialpreview.util.hide
import com.ekosoftware.financialpreview.util.show

class EditFrequencyFragment : Fragment() {

    private var _binding: EditFragmentFrequencyBinding? = null
    private val binding get() = _binding!!

    private val args: EditFrequencyFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EditFragmentFrequencyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.frequency != null) {
            val frequency = args.frequency
        }

        arrayOf(binding.chipRepeat, binding.chipNotRepeat, binding.chipInstallments).forEach {
            it.setOnClickListener {
                toggleMode()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun toggleMode() = with(binding) {
        when {
            binding.chipNotRepeat.isChecked -> toggleVisibility(whenL = true)
            binding.chipRepeat.isChecked -> toggleVisibility(from = true, to = true, months = true)
            binding.chipInstallments.isChecked -> toggleVisibility(from = true, installments = true)
        }
    }

    private fun toggleVisibility(
        whenL: Boolean = false,
        from: Boolean = false,
        to: Boolean = false,
        installments: Boolean = false,
        months: Boolean = false
    ) = with(binding) {
        if (whenL) layoutWhen.show() else layoutWhen.hide()
        if (from) layoutFrom.show() else layoutFrom.hide()
        if (to) layoutTo.show() else layoutTo.hide()
        if (installments) {
            layoutInstallmentsAmount.show()
            layoutInstallmentsResult.show()
        } else {
            layoutInstallmentsAmount.hide()
            layoutInstallmentsResult.hide()
        }
        if (months) layoutMonths.show() else layoutMonths.hide()
    }

}